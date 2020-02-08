package me.naptie.bukkit.player.utils;


import me.naptie.bukkit.player.commands.Language;
import me.naptie.bukkit.player.Main;
import me.naptie.bukkit.player.Messages;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * 邮箱检测主要实现
 * 
 * @author yuanlu
 *
 */
public final class EmailChecker {
    
    /**
     * 邮件数据
     * 
     * @author yuanlu
     *
     */
    private static final class MailData {
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "time:" + endTime + ", code: " + verificationCode;
        }
        
        /**
         * 过期时间
         */
        final long endTime;
        
        /**
         * 正确的验证码
         */
        final String verificationCode;
        
        /**
         * 构造一个邮件数据
         * 
         * @param endTime
         *            过期时间
         * @param verificationCode
         *            验证码
         */
        MailData(long endTime, String verificationCode) {
            this.endTime = endTime;
            this.verificationCode = verificationCode;
        }
    }
    
    /**
     * 连接
     */
    private static Session session;
    
    /**
     * 邮件发送者
     */
    private static InternetAddress from;
    
    /**
     * 所有的 用户名 - 验证码及过期时间
     */
    private static final LinkedHashMap<String, MailData> MAILS = new LinkedHashMap<>();
    
    /**
     * 所有的 用户名 - 冷却时间
     */
    private static final LinkedHashMap<String, Long> COOL_DOWN_TIME = new LinkedHashMap<>();
    
    /**
     * 随机数
     */
    private static final Random R = new Random();

	/**
	 * 保存玩家的邮箱地址信息
	 */
	public static Map<Player, String> emailAddresses = new HashMap<>();
    
    /**
     * 冷却时间,单位ms
     */
    private static long coolDownTime;
    
    /**
     * 验证码有效时间,单位ms
     */
    private static long effectiveTime;
	/**
	 * 日期格式化
	 */
	private static Map<String, SimpleDateFormat> SDF = new HashMap<>();
    
    /**
     * 测试某玩家是否能够发送验证码, 即无冷却状态
     * 
     * @param p
     *            玩家
     * @return 是否能发送验证码
     */
    private static boolean canSend(Player p) {
        Long ___ = COOL_DOWN_TIME.get(p.getName());
        if (___ == null) return true;
        long __ = System.currentTimeMillis();
        return ___ <= __;
    }
    
    /**
     * 检查玩家验证码是否正确
     * 
     * @param p
     *            玩家
     * @param pvc
     *            验证码
     * @return 是否正确<br>
     *         true: 验证成功<br>
     *         false: 验证失败/超时/未发送邮件
     */
    public static boolean check(Player p, String pvc) {
        MailData md = MAILS.get(p.getName());
        if (md == null || md.endTime < System.currentTimeMillis()) return false;
        return pvc.equals(md.verificationCode);
    }
    
    /**
     * 向玩家输入的邮箱发送验证码
     * 
     * @param p
     *            玩家
     * @param address
     *            邮箱
     * @return 状态码<br>
     *         0: 正常发送<br>
     *         -1: 邮箱不正确<br>
     *         -2: 冷却中
     * @throws MessagingException
     *             发送邮件时出错
     */
    public synchronized static byte email(Player p, String address) throws MessagingException {
        if (canSend(p)) {
            InternetAddress TO;
            try {
                TO = new InternetAddress(address);
            } catch (AddressException e) {
                return -1;
            }
            String Subject, code, Text;
            long time;
            time = System.currentTimeMillis();
	        Subject = Messages.getMessage(p, "MAIL_SUBJECT")
			        .replace("%player%", p.getName()).replace("%n%", "\n\n");
	        code = getVerificationCode();
	        Text = Messages.getMessage(p, "MAIL_TEXT")
			        .replace("%time%", SDF.get(ConfigManager.getLanguageName(p)).format(new Date(time)))//
			        .replace("%player%", p.getName())//
			        .replace("%code%", code).replace("%nn%", "\n\n").replace("%n%", "\n");
            
            MailData md = new MailData(time + effectiveTime, code);
            MAILS.put(p.getName(), md);
            COOL_DOWN_TIME.put(p.getName(), time + coolDownTime);
            
            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, TO);
            message.setSubject(Subject);
            message.setText(Text);
            message.setFrom(from);
            Transport.send(message);
	        emailAddresses.put(p, address.toLowerCase());
            return 0;
        }
        return -2;
    }
    
    /**
     * 创建验证码
     * 
     * @return 随机验证字符串
     */
    private static String getVerificationCode() {
	    long a = R.nextLong();
	    char[] cs = (Long.toUnsignedString(a, 32)).toCharArray();
	    for (int i = 0; i < cs.length; i++) {
		    if (cs[i] >= 'a' && cs[i] <= 'z' && R.nextBoolean()) cs[i] -= 32;
	    }
	    return new String(cs);
    }
    
    /**
     * 初始化
     * 
     * @param config
     *            配置
     */
    public static void init(ConfigurationSection config) {
	    for (String language : Language.supportedLanguages) {
		    String dateFormat = config.getString("time-format." + language, "yyyy/MM/dd HH:mm:ss");
		    SDF.put(language, new SimpleDateFormat(dateFormat));
	    }
        coolDownTime = config.getLong("cool-down-time", 60 * 1000);
        effectiveTime = config.getLong("effective-time", 60 * 5 * 1000);
        String username = config.getString("email-address");
        String password = config.getString("password");
        try {
            from = new InternetAddress(username);
        } catch (AddressException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        new BukkitRunnable() {
            
            ArrayList<String> clear = new ArrayList<>();
            
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                
                try {
                    for (Entry<String, MailData> e : MAILS.entrySet()) {
                        if (e.getValue().endTime < time) clear.add(e.getKey());
                    }
                } catch (ConcurrentModificationException ignored) {
                }
                for (String key : clear)
                    MAILS.remove(key);
                clear.clear();
                
                try {
                    for (Entry<String, Long> e : COOL_DOWN_TIME.entrySet()) {
                        if (e.getValue() < time) clear.add(e.getKey());
                    }
                } catch (ConcurrentModificationException ignored) {
                }
                for (String key : clear)
                    COOL_DOWN_TIME.remove(key);
                clear.clear();
                
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20 * 60 * 60, 20 * 60 * 60);
    }
}
