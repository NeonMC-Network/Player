package me.naptie.bukkit.player.commands;

import me.naptie.bukkit.player.Messages;
import me.naptie.bukkit.player.Permissions;
import me.naptie.bukkit.player.events.ExperienceUpdateEvent;
import me.naptie.bukkit.player.utils.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Experience implements CommandExecutor {

    @SuppressWarnings({"deprecation", "NullableProblems"})
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(Permissions.EXPERIENCE)) {
                if (args.length == 1) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    if (target != null) {
                        player.sendMessage(Messages.getMessage(player, "EXPERIENCE").replace("%player%", target.getName()).replace("%point%", ConfigManager.getData(target).getString("point")).replace("%level%", ConfigManager.getLevel(target)));
                        return true;
                    } else {
                        player.sendMessage(Messages.getMessage(player, "PLAYER_NOT_FOUND").replace("%player%", args[0]));
                        return true;
                    }

                } else if (args.length == 2) {
                    String operation = args[0];
                    int amount;
                    if (isValid(args[1])) {
                        amount = Integer.parseInt(args[1]);
                    } else {
                        player.sendMessage(Messages.getMessage(player, "NOT_NUMERIC").replace("%string%", args[1]));
                        return true;
                    }
                    if (operation.equalsIgnoreCase("set")) {
                        int previous = ConfigManager.getData(player).getInt("point");
                        ConfigManager.getData(player).set("point", amount);
                        try {
                            ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(player, previous, amount));
                        player.sendMessage(Messages.getMessage(player, "SET_POINT_SELF_SUCCESS").replace("%point%", amount + ""));
                        return true;
                    }
                    if (operation.equalsIgnoreCase("add")) {
                        int previousPoint = ConfigManager.getData(player).getInt("point");
                        if (isValid(previousPoint, amount)) {
                            ConfigManager.getData(player).set("point", previousPoint + amount);
                            try {
                                ConfigManager.getData(player).save(ConfigManager.getDataFile(player));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(player, previousPoint, previousPoint + amount));
                            player.sendMessage(Messages.getMessage(player, "ADD_POINT_SELF_SUCCESS").replace("%amount%", amount + "").replace("%previous%", previousPoint + "").replace("%point%", previousPoint + amount + ""));
                            return true;
                        } else {
                            player.sendMessage(Messages.getMessage(player, "VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }

                } else if (args.length == 3) {
                    String operation = args[0];
                    int amount;
                    if (isValid(args[1])) {
                        amount = Integer.parseInt(args[1]);
                    } else {
                        player.sendMessage(Messages.getMessage(player, "NOT_NUMERIC").replace("%string%", args[1]));
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    if (target != null) {
                        if (operation.equalsIgnoreCase("set")) {
                            int previous = ConfigManager.getData(target).getInt("point");
                            ConfigManager.getData(target).set("point", amount);
                            try {
                                ConfigManager.getData(target).save(ConfigManager.getDataFile(target));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(target, previous, amount));
                            player.sendMessage(Messages.getMessage(player, "SET_POINT_SUCCESS").replace("%player%", target.getName()).replace("%point%", amount + ""));
                            return true;
                        }
                        if (operation.equalsIgnoreCase("add")) {
                            int previousPoint = ConfigManager.getData(target).getInt("point");
                            if (isValid(previousPoint, amount)) {
                                ConfigManager.getData(target).set("point", previousPoint + amount);
                                try {
                                    ConfigManager.getData(target).save(ConfigManager.getDataFile(target));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(target, previousPoint, previousPoint + amount));
                                player.sendMessage(Messages.getMessage(player, "ADD_POINT_SUCCESS").replace("%amount%", amount + "").replace("%player%", target.getName()).replace("%previous%", previousPoint + "").replace("%point%", previousPoint + amount + ""));
                                return true;
                            } else {
                                player.sendMessage(Messages.getMessage(player, "VALUE_OUT_OF_RANGE"));
                                return true;
                            }
                        }
                    } else {
                        player.sendMessage(Messages.getMessage(player, "PLAYER_NOT_FOUND").replace("%player%", args[2]));
                        return true;
                    }
                } else {
                    player.sendMessage(Messages.getMessage(player, "USAGE").replace("%usage%", "/experience set|add <amount> [player] OR /experience <player>"));
                    return true;
                }
            } else {
                player.sendMessage(Messages.getMessage(player, "PERMISSION_DENIED"));
            }
            return true;
        } else {
            if (args.length == 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (target != null) {
                    sender.sendMessage(Messages.getMessage("zh-CN", "EXPERIENCE").replace("%player%", target.getName()).replace("%point%", ConfigManager.getData(target).getString("point")).replace("%level%", ConfigManager.getLevel(target)));
                    return true;
                } else {
                    sender.sendMessage(Messages.getMessage("zh-CN", "PLAYER_NOT_FOUND").replace("%player%", args[0]));
                    return true;
                }

            } else if (args.length == 3) {
                String operation = args[0];
                int amount;
                if (isValid(args[1])) {
                    amount = Integer.parseInt(args[1]);
                } else {
                    sender.sendMessage(Messages.getMessage("zh-CN", "NOT_NUMERIC").replace("%string%", args[2]));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (target != null) {
                    if (operation.equalsIgnoreCase("set")) {
                        int previous = ConfigManager.getData(target).getInt("point");
                        ConfigManager.getData(target).set("point", amount);
                        try {
                            ConfigManager.getData(target).save(ConfigManager.getDataFile(target));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(target, previous, amount));
                        sender.sendMessage(Messages.getMessage("zh-CN", "SET_POINT_SUCCESS").replace("%player%", target.getName()).replace("%point%", amount + ""));
                        return true;
                    }
                    if (operation.equalsIgnoreCase("add")) {
                        int previousPoint = ConfigManager.getData(target).getInt("point");
                        if (isValid(previousPoint, amount)) {
                            ConfigManager.getData(target).set("point", previousPoint + amount);
                            try {
                                ConfigManager.getData(target).save(ConfigManager.getDataFile(target));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new ExperienceUpdateEvent(target, previousPoint, previousPoint + amount));
                            sender.sendMessage(Messages.getMessage("zh-CN", "ADD_POINT_SUCCESS").replace("%amount%", amount + "").replace("%player%", target.getName()).replace("%previous%", previousPoint + "").replace("%point%", previousPoint + amount + ""));
                            return true;
                        } else {
                            sender.sendMessage(Messages.getMessage("zh-CN", "VALUE_OUT_OF_RANGE"));
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage(Messages.getMessage("zh-CN", "PLAYER_NOT_FOUND").replace("%player%", args[2]));
                    return true;
                }
            } else {
                sender.sendMessage(Messages.getMessage("zh-CN", "USAGE").replace("%usage%", "'experience set|add <amount> <player>' OR 'experience <player>'"));
                return true;
            }
            return true;
        }
    }

    private boolean isValid(String input) {
        if (StringUtils.isNumeric(input)) {
            if (input.toCharArray().length < 10) {
                return Integer.parseInt(input) > 0;
            } else if (input.toCharArray().length == 10) {
                int i = Integer.parseInt(String.valueOf(input.charAt(0)));
                if (i < 2) {
                    return true;
                } else if (i == 2) {
                    return Integer.parseInt(String.valueOf(input.charAt(1))) <= 1 && Integer.parseInt(String.valueOf(input.charAt(2))) <= 3;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isValid(int input1, int input2) {
        int result;
        try {
            result = input1 + input2;
        } catch (Exception e) {
            return false;
        }
        return StringUtils.isNumeric(result + "") && result <= 2139999999;
    }
}
