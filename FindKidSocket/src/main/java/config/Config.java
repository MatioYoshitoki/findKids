package config;

import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import model.Gps_msg;
import model.Gps_receive;
import model.Gps_tmp;
import model.Protect;
import org.apache.log4j.Logger;

/**
 * Created by matioyoshitoki on 2017/9/18.
 */
public class Config {

    private Logger logger = Logger.getLogger(getClass());

    public void initAll() throws IOException {
        initPid();
        initDB();
    }

    // 初始化pid文件
    private void initPid() throws IOException {
        FileOutputStream out = new FileOutputStream(new File("pid"));
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

        out.write(pid.getBytes());
        out.close();

        logger.warn("finish initPid()");
    }

    // 初始化jfinal-ext的db连接
    private void initDB() throws IOException {
        Properties p = new Properties();
        p.load(getClass().getResourceAsStream("/db.properites"));

        DruidPlugin dp = new DruidPlugin(p.getProperty("db"), p.getProperty("user"), p.getProperty("password"));
        dp.set(Integer.valueOf(p.getProperty("initialSize")), Integer.valueOf(p.getProperty("minIdle")),
                Integer.valueOf(p.getProperty("maxActive")));
        dp.setRemoveAbandonedTimeoutMillis(6 * 1000);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.addMapping("gps_tmp", Gps_tmp.class);
        arp.addMapping("gps_msg", Gps_msg.class);
        arp.addMapping("gps_receive", Gps_receive.class);
        arp.addMapping("protect", Protect.class);

        dp.start();
        arp.start();

        logger.warn("finish initDB()");
    }
}
