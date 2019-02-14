package com.fuge.rimixplus.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.fuge.excel.style.FugeStyle;
import com.fuge.jfinal.tools.api.ApiDocUtil;
import com.fuge.jfinal.tools.core.param.FieldParamPro;
import com.fuge.jfinal.tools.handler.XssHandler;
import com.fuge.jfinal.tools.interceptor.ResponseInterceptor;
import com.fuge.jfinal.tools.plugin.FugePlugin;
import com.fuge.rimixplus.auth.SigninController;
import com.fuge.rimixplus.login.LoginController;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.ext.plugin.sqlinxml.SqlInXmlPlugin;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.LogbackFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

import java.util.ArrayList;
import java.util.List;

import static com.fuge.rimixplus.config.AppConfigUtil.addMapping;
import static com.fuge.rimixplus.config.AppConfigUtil.addSqlMapping;


/**
 * App Config
 *
 * @author jida
 */
public class AppConfig extends JFinalConfig {

    private Routes routes = null;

    @Override
    public void configConstant(Constants constants) {
        loadPropertyFile("conf/rimixplus.properties");
        PropKit.use("conf/rimixplus.properties");
        constants.setDevMode(PropKit.getBoolean("devMode", true));
        constants.setViewType(ViewType.JFINAL_TEMPLATE);
        constants.setLogFactory(new LogbackFactory());
        constants.setMaxPostSize(1024 * 1024 * 1024);
        constants.setBaseUploadPath(PathKit.getWebRootPath() + getProperty("upload.path"));
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", LoginController.class);

        this.routes = me;
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
//        DruidPlugin druidPlugin = getDruidPlugin();
//        plugins.add(druidPlugin);
//
//        // add fuge plugin
//        addFuGePlugin(plugins);

        //定时任务
//        QuartzPlugin quartzPlugin = new QuartzPlugin("conf/job.properties");
//        plugins.add(quartzPlugin);
//
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//        addMapping(arp);

//        arp.setDialect(new MysqlDialect());
//        arp.setShowSql(true);
//        plugins.add(arp);
//        SqlReporter.setLog(true);
//
//        // 设置从common的jar中读取sql
//        arp.getEngine().setBaseTemplatePath(null);
//        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
//        addSqlMapping(arp);
//
//        //sql语句plugin
//        plugins.add(new SqlInXmlPlugin("sql"));
    }

    private DruidPlugin getDruidPlugin() {
        DruidPlugin druidPlugin = new DruidPlugin(getProperty("db.default.url"),
                getProperty("db.default.user"),
                getProperty("db.default.password"),
                getProperty("db.default.driver"));

        // StatFilter提供JDBC层的统计信息
        final StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(2000);
        druidPlugin.addFilter(statFilter);

        // WallFilter的功能是防御SQL注入攻击
        WallFilter wallDefault = new WallFilter();
        wallDefault.setDbType("mysql");

        druidPlugin.addFilter(wallDefault);
        druidPlugin.setInitialSize(getPropertyToInt("db.default.poolInitialSize"));
        druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(getPropertyToInt("db.default.poolMaxSize"));
        druidPlugin.setTimeBetweenConnectErrorMillis(getPropertyToInt("db.default.connectionTimeoutMillis"));

        return druidPlugin;
    }

    /**
     * 设置fuge插件
     *
     * @param plugins 插件列表
     */
    private void addFuGePlugin(Plugins plugins) {
        FugePlugin fugePlugin = new FugePlugin();
        fugePlugin.setPlugin(new FieldParamPro());

        FugeStyle.init( "conf/report-style-library.json");

        plugins.add(fugePlugin);
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new ResponseInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        List<String> whiteList = new ArrayList<>();
        whiteList.add("&");
        whiteList.add("\n");

        me.add(new XssHandler(this.routes, whiteList));
    }

    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 8080, "/");
    }
}
