## 秒杀接口设计与实现
>模拟秒杀场景，设计实现一个秒杀接口，应对高并发场景提出一些优化解决方案。

在设计秒杀接口时，主要需要考虑两个问题：
- 如何解决超卖问题？
- 如何尽可能地减少数据库的访问压力？

### 超卖问题
对于超卖问题，其产生的原因如下：

当多线程访问时，假设当前商品库存还有一个，同时过来两个线程，A线程判断商品数大于0，B线程也判断商品数大于0，则两个线程都继续向下执行。若正巧都秒杀到，则执行减库存下订单操作就将库存减成负数了。

超卖问题如何解决呢？

- 情况一：一个用户同时发出多个请求，如果库存足够，未加限制，用户可以下多个订单。
>解决：前端加验证码，防止用户同时发出多个请求。在后端的`seckill_order`表中，对`user_id`和`goods_id`加唯一索引，确保一个用户对一个商品绝不生成两个订单。

- 情况二：减库存的SQL上没有添加库存数量的判断，并发时也会将库存减成负数。
>解决：减库存的SQL上添加库存数量的判断:
```
update t_seckill_goods 
set stock_count = stock_count - 1 
where goods_id = #{goodsId} and stock_count > 0
```

### 高并发问题
秒杀业务的特点在于瞬间的并发量非常大，同时它也是一种读多写少的场景，往往采用`缓存`和`异步化`的方式来解决。主要考虑以下几点：
- 尽量将请求过滤在上游
- 尽可能地利用缓存
- 异步下单增加吞吐量

对于此项目而言，其秒杀流程如下图所示：
![秒杀流程](https://github.com/WavyPeng/SeckillDesign/blob/master/src/main/resources/images/%E7%A7%92%E6%9D%80%E6%B5%81%E7%A8%8B.jpg)

部分功能的具体实现细节如下：

1、点击秒杀前，先输入验证码，分散用户请求

(1)前端：添加生成验证码的接口
```
// 添加图形验证码
$("#verifyCodeImg").attr("src","/goods/seckill/verifyCode?goodsId="+$("#goodsId").val());
$("#verifyCodeImg").show();
$("#verifyCode").show();
```

(2)后端

生成验证码
```
public BufferedImage createVerifyCode(User user,long goodsId){
    if(user == null || goodsId<=0){
        return null;
    }
    int width = 80;   // 宽
    int height = 32;  // 高
    // 创建图像
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    // 设置背景颜色
    g.setColor(new Color(0xDCDCDC));
    g.fillRect(0, 0, width, height);
    // 绘制边
    g.setColor(Color.black);
    g.drawRect(0, 0, width - 1, height - 1);
    // 随机对象
    Random rdm = new Random();
    // 制造噪点
    for (int i = 0; i < 50; i++) {
        int x = rdm.nextInt(width);
        int y = rdm.nextInt(height);
        g.drawOval(x, y, 0, 0);
    }
    // 生成验证码
    String verifyCode = VerifyCodeUtil.generateVerifyCode(rdm);
    g.setColor(new Color(0, 100, 0));
    g.setFont(new Font("Candara", Font.BOLD, 24));
    g.drawString(verifyCode, 8, 24);
    g.dispose();
    // 把验证码存到redis中
    int rnd = VerifyCodeUtil.calculate(verifyCode);
    redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+"_"+goodsId, rnd);
    // 返回验证码
    return image;
}
```

计算公式验证码结果，采用`ScriptEngine`
```
public static int calculate(String exp){
    try{
        // 创建ScriptEngineManager
        ScriptEngineManager manager = new ScriptEngineManager();
        // 获取ScriptEngine对象
        ScriptEngine engine = manager.getEngineByName("js");
        return (Integer)engine.eval(exp);
    }catch (Exception e){
        e.printStackTrace();
        return 0;
    }
}
```

2、动态生成秒杀路径并存入Redis，利用`UUID.randomUUID()`

```
public static String uuid(){
    //将原生UUID的'-'去掉
    return UUID.randomUUID().toString().replace("-","");
}

/**
 * 生成秒杀路径
 * @param user
 * @param goodsId
 * @return path
 */
public String createSeckillPath(User user,long goodsId){
    if(user == null || goodsId<=0){
        return null;
    }
    String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
    redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,path);
    return path;
}
```

3、接口限流防刷

定义拦截器`AccessInterceptor`，将用户访问接口的次数写入缓存，同时给数据加一个有效期（如1分钟），有效期内再次访问，将访问次数加1。如果在有效期内超过限定次数，则返回失败；如果未超过次数，则下一分钟开始再次从0计算。具体实现如下：

```
// 定义拦截器
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (handler instanceof HandlerMethod) {
        ......
        // 创建AccessKey前缀
        AccessKey accessKey = AccessKey.withExpire(seconds);
        // 查询访问次数
        Integer count = redisService.get(accessKey, key, Integer.class);
        if(count == null) {  // 说明首次访问
            redisService.set(accessKey, key, 1);
        }else if(count < maxCount) {
            redisService.incr(accessKey, key);
        }else {  // 访问超限
            render(response, CodeMsg.ACCESS_LIMIT_REACHED);
            return false;
        }
    }
    return true;
}

// 添加拦截器
public class WebConfig extends WebMvcConfigurerAdapter {
    ......
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
```

4、RabbitMQ异步处理秒杀

(1)Controller层
```
/**
 * RabbitMQ异步处理秒杀
 * @param model
 * @param user
 * @param goodsId
 * @return
 */
@RequestMapping(value = "/rabbitmq/{path}/do_seckill",method = RequestMethod.POST)
@ResponseBody
public Result<Integer> rabbitmqForDoSeckill(Model model, User user,
                                            @RequestParam("goodsId")long goodsId,
                                            @PathVariable("path") String path){
    model.addAttribute("user",user);
    if(user == null){
        return Result.error(CodeMsg.SESSION_ERROR);
    }

    // ----- 安全优化 -----
    // 判断秒杀路径
    boolean check = seckillService.checkPath(path,user,goodsId);
    if(!check){
        return Result.error(CodeMsg.REQUEST_ILLEGAL);
    }
    // ------ END ------

    // ------ 优化 ------
    // 判断商品秒杀状态
    boolean isOver = localOverMap.get(goodsId);
    if(isOver){  // 说明已经秒杀完毕，直接返回
        return Result.error(CodeMsg.SECKILL_OVER);
    }
    // ------ 内存标记，减少redis访问 ------

    // 预减库存
    long stock = redisService.decr(GoodsKey.getSeckillStock,""+goodsId);
    if(stock < 0){
        localOverMap.put(goodsId,true); // 标记商品已经秒杀完了
        return Result.error(CodeMsg.SECKILL_OVER);
    }
    // 判断是否重复秒杀
    SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
    if(seckillOrder != null){
        return Result.error(CodeMsg.SECKILL_REPEAT);
    }
    // 入队
    SeckillMessage seckillMessage = new SeckillMessage();
    seckillMessage.setUser(user);
    seckillMessage.setGoodsId(goodsId);
    sender.sendSeckillMessage(seckillMessage);
    return Result.success(0); //排队中
}
```

(2)RabbitMQ消息队列
```
/**
 * 发送秒杀消息（采用Direct模式）
 * @param seckillMessage
 */
public void sendSeckillMessage(SeckillMessage seckillMessage){
    String msg = ConversionUtil.beanToString(seckillMessage);
    log.info("send message:{}",msg);
    //指定发送到哪个队列
    amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);
}

/**
 * 接收秒杀消息，执行异步下单
 * @param message
 */
@RabbitListener(queues = MQConfig.SECKILL_QUEUE)
public void seckillReceive(String message){
    log.info("seckill queue receive message:{}",message);

    // 解析接收到的消息
    SeckillMessage seckillMessage = ConversionUtil.stringToBean(message,SeckillMessage.class);
    User user = seckillMessage.getUser();
    long goodsId = seckillMessage.getGoodsId();

    // 获取商品信息 判断库存
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    long stock = goods.getStockCount();
    if(stock <= 0){
        return;
    }

    // 判断是否重复秒杀
    SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
    if(null != order){
        return;
    }

    // 减库存 下订单 记录秒杀订单
    seckillService.seckill(user,goods);
}
```

### 开发环境
IntelliJ IDEA 15.0.6

JDK1.8

### 技术栈
- 前端：Thymeleaf
- 后端：Spring Boot、MyBatis
- 中间件：RabbitMQ、Redis

### 项目结构
![项目结构](https://github.com/WavyPeng/SeckillDesign/blob/master/src/main/resources/images/%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84.jpg)

