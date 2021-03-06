package me.naptie.bukkit.player.commands.utils;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;


/**
 * 命令<br>
 * 命令处理的总类<br>
 * 提供方法: <br>
 * {@link #onCommand(CommandSender, Command, String, String[], int) 命令处理}<br>
 * {@link #onTabComplete(CommandSender, Command, String, String[], int)
 * tab补全}<br>
 * 采用树形结构进行命令处理<br>
 * 根命令传入时args处理位置为0, 继承命令根据使用的参数数量自行在传递到下一层时增加相应的数量
 * 
 * @author yuanlu
 *
 */
public abstract class AbstractCommand {
    
    /**
     * 空的tab列表<br>
     * 使用{@link Arrays#asList(Object...)}创建<br>
     * 大小为0, 不包含任何元素<br>
     * 防止自行创建的空列表浪费不必要的内存
     */
    protected static final List<String> EMPTY_TAB_LIST = Arrays.asList();
    
    /**
     * 执行命令<br>
     * 由上级(或命令源)触发<br>
     * argsIndex从0开始, 根据命令节点读取数量, 在传递给下层命令时自动增加对应数量(约定)
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
     * @return 是否成功执行
     */
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, int argsIndex);
    
    /**
     * 用命令传递的参数请求可能的补全项的list.
     * 
     * @param sender
     *            发起命令的来源. 至于玩家在命令方块内补全命令，这个来源就是玩家，而不是命令方块.
     * @param command
     *            执行的Command
     * @param label
     *            使用的命令别名
     * @param args
     *            传递给这个命令的参数，包括用来补全的部分最终参数和命令别名
     * @param argsIndex
     *            命令参数读取起点位置<br>
     *            根命令从0开始
     * @return 可能的最终补全参数列表(就是list里都是已经补全了的)，或为null则传递给命令执行器
     */
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args,
            int argsIndex);
}
