package com.fuge.rimixplus.login;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import org.junit.Test;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.UnknownHostException;
import java.util.Hashtable;

/**
 * Created by YSZ on 2019/2/13.
 */
public class LoginTest {

    @Test
    public void test() {
        String userName = "test";//AD域认证，用户的登录UserName
        String password = "Yu123456.";//AD域认证，用户的登录PassWord
        String host = "192.168.0.12";//AD域IP，必须填写正确
        String domain = "@test.domain.com";//域名后缀，例.@noker.cn.com
        String port = "389"; //端口，一般默认389
        String url = new String("ldap://" + host + ":" + port);//固定写法
        String user = userName.indexOf(domain) > 0 ? userName : userName
                + domain;//网上有别的方法，但是在我这儿都不好使，建议这么使用
        Hashtable env = new Hashtable();//实例化一个Env
        DirContext ctx = null;
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//LDAP访问安全级别(none,simple,strong),一种模式，这么写就行
        env.put(Context.SECURITY_PRINCIPAL, user); //用户名
        env.put(Context.SECURITY_CREDENTIALS, password);//密码
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");// LDAP工厂类
        env.put(Context.PROVIDER_URL, url);//Url
        try {
            ctx = new InitialDirContext(env);// 初始化上下文
            System.out.println("身份验证成功!");
        } catch (AuthenticationException e) {
            System.out.println("身份验证失败!");
            e.printStackTrace();
        } catch (javax.naming.CommunicationException e) {
            System.out.println("AD域连接失败!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("身份验证未知异常!");
            e.printStackTrace();
        } finally{
            if(null!=ctx){
                try {
                    ctx.close();
                    ctx=null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void test2() throws SmbException, UnknownHostException {

        UniAddress thisAddress = UniAddress.getByName("192.168.0.12"); //host为IP地址或服务器名称，String
        NtlmPasswordAuthentication thisAuth = new NtlmPasswordAuthentication("", "test",
                "Yu123456!");//domain 为windows 的域
        SmbSession.logon(thisAddress, thisAuth);//验证成功返回tru
    }

    @Test
    public void test3() {
        System.out.println(System.getProperty("user.name"));;
    }
}
