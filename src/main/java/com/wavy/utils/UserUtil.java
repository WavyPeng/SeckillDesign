package com.wavy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wavy.entity.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 生成用户工具类
 * Created by WavyPeng on 2018/5/12.
 */
public class UserUtil {
    private static void createUser(int count) throws Exception{
        List<User> users = new ArrayList<User>(count);
        //生成用户
        for(int i=0;i<count;i++) {
            User user = new User();
            user.setId(13000000000L+i);
            user.setLogin_count(1);
            user.setNickname("user"+i);
            user.setRegister_date(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");
		//插入数据库
//		Connection conn = DBUtil.getConn();
//		String sql = "insert into t_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			User user = users.get(i);
//			pstmt.setInt(1, user.getLogin_count());
//			pstmt.setString(2, user.getNickname());
//			pstmt.setTimestamp(3, new Timestamp(user.getRegister_date().getTime()));
//			pstmt.setString(4, user.getSalt());
//			pstmt.setString(5, user.getPassword());
//			pstmt.setLong(6, user.getId());
//			pstmt.addBatch();
//		}
//		pstmt.executeBatch();
//		pstmt.close();
//		conn.close();
//		System.out.println("insert to db");
        //登录，生成token
        String urlString = "http://localhost:8080/user/do_login";
        File file = new File("D:/seckill/tokens.txt");
        if(file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for(int i=0;i<users.size();i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection)url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0 ,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            JSONObject jo = JSON.parseObject(response);
            String token = jo.getString("data");
            System.out.println("create token : " + user.getId());
            System.out.println("token :" + token);

            String row = user.getId()+","+token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

        System.out.println("over");
    }

    public static void main(String[] args)throws Exception {
        createUser(5000);
    }
}