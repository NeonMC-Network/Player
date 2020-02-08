package me.naptie.bukkit.player.commands.utils;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


/**
 * 检查命令来源类型的嵌套命令执行器<br>
 * 一般用于限定控制台或玩家
 * 
 * @author yuanlu
 *
 */
public final class CheckSenderCommand extends FilterCommand {
    
    /**
     * 检查类型
     * 
     * @author yuanlu
     *
     */
    public enum CheckType {
        /**
         * 传入的{@link CommandSender}是{@link CheckSenderCommand#clazz}的子类
         */
        EXTENDS,
        /**
         * 传入的{@link CommandSender}等于{@link CheckSenderCommand#clazz}
         */
        EQUAL,
        /**
         * 传入的{@link CommandSender}不是是{@link CheckSenderCommand#clazz}的子类
         */
        NOTEXTENDS,
        /**
         * 传入的{@link CommandSender}不等于{@link CheckSenderCommand#clazz}
         */
        NOTEQUAL
    }
    
    /**
     * 类的类型名Hash图<br>
     * 缓存常量
     */
    public static final HashMap<Class<? extends CommandSender>, String> TYPE_NAME;
    
    /**
     * 类的名称Hash图<br>
     * 缓存常量
     */
    public static final HashMap<Class<? extends CommandSender>, String> SIMPLE_NAME;
    /**
     * 是否启用名称缓存图
     */
    public static final boolean USE_NAME_CACHE = true;
    
    /**
     * 初始化是否使用缓存
     */
    static {
        if (USE_NAME_CACHE) {
            TYPE_NAME = new HashMap<>();
            SIMPLE_NAME = new HashMap<>();
        } else {
            TYPE_NAME = null;
            SIMPLE_NAME = null;
        }
    }
    
    /**
     * 检测的类
     */
    public final Class<? extends CommandSender> clazz;
    
    /**
     * 检测类型
     */
    public final CheckType type;
    
    /**
     * 检测没有通过时发出的信息(默认)
     */
    public final String MES_NOT_PASS;
    
    /**
     * 构造一个全参数的命令来源类型检查器
     * 
     * @param next
     *            在命令通过过滤时执行命令执行器
     * @param clazz
     *            检测的类
     * @param type
     *            检测类型
     * @param mes_notPass
     *            检测未通过时的信息
     */
    public CheckSenderCommand(AbstractCommand next, Class<? extends CommandSender> clazz, CheckType type,
            String mes_notPass) {
        super(next);
        if (clazz == null) throw new NullPointerException("clazz cannot be null.");
        if (type == null) throw new NullPointerException("type cannot be null.");
        if (mes_notPass == null) throw new NullPointerException("mes_notPass cannot be null.");
        this.clazz = clazz;
        this.type = type;
        this.MES_NOT_PASS = mes_notPass;
    }
    
    /**
     * 构造一个命令来源是否是玩家的检查器
     * 
     * @param next
     *            在命令通过过滤时执行命令执行器
     * @param mes_notPass
     *            检测未通过时的信息
     */
    public CheckSenderCommand(AbstractCommand next, String mes_notPass) {
        this(next, Player.class, CheckType.EXTENDS, mes_notPass);
    }
    
    @Override
    public final boolean check(CommandSender sender, Command cmd, String label, String[] args, int argsIndex) {
        boolean ok = false;
        Class<? extends CommandSender> c = sender.getClass();
        switch (type) {
        case EQUAL:
            ok = c == clazz;
            break;
        case EXTENDS:
            ok = clazz.isAssignableFrom(c);
            break;
        case NOTEQUAL:
            ok = c != clazz;
            break;
        case NOTEXTENDS:
            ok = !clazz.isAssignableFrom(c);
            break;
        }
        if (!ok) return noArgsCase(sender, cmd, label, args, argsIndex, c);
        return true;
    }
    
    /**
     * 获取类的SimpleName
     * 
     * @param c
     *            类
     * @return SimpleName
     */
    protected final String getCSname(Class<? extends CommandSender> c) {
        if (!USE_NAME_CACHE) return c.getSimpleName();
        String name = SIMPLE_NAME.get(c);
        if (name == null) {
            name = c.getSimpleName();
            SIMPLE_NAME.put(c, name);
        }
        return name;
    }
    
    /**
     * 获取类的TypeName
     * 
     * @param c
     *            类
     * @return TypeName
     */
    protected final String getCTname(Class<? extends CommandSender> c) {
        if (!USE_NAME_CACHE) return c.getTypeName();
        String name = TYPE_NAME.get(c);
        if (name == null) {
            name = c.getTypeName();
            TYPE_NAME.put(c, name);
        }
        return name;
    }
    
    /**
     * 当命令执行的来源类型不符合预设的类型时调用此方法<br>
     * 若没有继承本类并重写本方法, 则将会把预设的{@link #MES_NOT_PASS}发给命令执行的来源<br>
     * 发送的信息将会进行如下的替换:<br>
     * <br>
     * %sender% {@code ->} 命令执行来源的类型名称(getSimpleName())<br>
     * %class% {@code ->} 命令执行来源的类型名称(getTypeName())<br>
     * <br>
     * 以上两个名称可能将会进行缓存
     * 
     * @param sender
     *            命令执行的来源
     * @param cmd
     *            被执行的命令
     * @param label
     *            使用的命令别名
     * @param args
     *            传递的命令参数
     * @param argsIndex
     *            命令参数读取起点位置<br>
     *            根命令从0开始
     * @param senderClass
     *            命令执行的来源的类
     * @return 向上传递测命令执行状态<br>
     *         此值将直接传给命令过滤器(若为true则继续执行), 而不会在本方法后执行其他语句
     */
    protected boolean noArgsCase(CommandSender sender, Command cmd, String label, String[] args, int argsIndex,
            Class<? extends CommandSender> senderClass) {
        sender.sendMessage(
                MES_NOT_PASS.replace("%sender%", getCSname(senderClass)).replace("%class%", getCTname(senderClass)));
        return true;
    }
}
