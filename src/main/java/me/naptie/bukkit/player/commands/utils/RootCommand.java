package me.naptie.bukkit.player.commands.utils;


import me.naptie.bukkit.player.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;


/**
 * 代表一个命令，在用户输入时执行各种任务.<br>
 * 根命令<br>
 * 继承自{@link Command}<br>
 * 内部调用{@link AbstractCommand}或其子类进行命令处理
 * 
 * @author yuanlu
 *
 */
public class RootCommand extends Command {
    
    /**
     * 处理方法
     */
    private final AbstractCommand handle;
    
    /**
     * @param name
     *            name
     * @param description
     *            description
     * @param usageMessage
     *            usageMessage
     * @param aliases
     *            aliases
     * @param handle
     *            handle
     */
    public RootCommand(String name, String description, String usageMessage,
            List<String> aliases, AbstractCommand handle) {
        super(name, description, usageMessage, aliases);
        this.handle = handle;
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return handle.onCommand(sender, this, alias, args, 0);
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return handle.onTabComplete(sender, this, alias, args, 0);
    }
    
    /**
     * 注册命令<br>
     * 将本命令注册到Bukkit上<br>
     * 由于部分核心(Paper以外的)没有放出{@code Bukkit.getCommandMap()}方法, 所以在发生错误时使用反射注册<br>
     * 由于本人对反射信任度不是很高, 所以做了2个错误打印, 提醒用户出现问题, 以便及时反馈<br>
     * <br>
     * <i>在此提醒各位码农, 未找到方法是Error而不是Exception, 最开始我就犯了这个错误</i>
     */
    public void register() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            CommandMap cmdm = (CommandMap) method.invoke(Bukkit.getServer());
            cmdm.register(Main.getInstance().getName(), this);
        } catch (Exception e2) {
            System.err.println("Could not register command: " + e2.toString());
            Main.getInstance().getLogger().warning("Could not register command: " + e2.toString());
        }
    }
}
