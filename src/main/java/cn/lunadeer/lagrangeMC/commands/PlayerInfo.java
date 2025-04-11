package cn.lunadeer.lagrangeMC.commands;

import cn.lunadeer.lagrangeMC.LagrangeMC;
import cn.lunadeer.lagrangeMC.managers.TemplateFactory;
import cn.lunadeer.lagrangeMC.managers.WebDriverManager;
import cn.lunadeer.lagrangeMC.protocols.GroupOperation;
import cn.lunadeer.lagrangeMC.protocols.PrivateOperation;
import cn.lunadeer.lagrangeMC.tables.WhitelistTable;
import cn.lunadeer.lagrangeMC.utils.XLogger;
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.util.UUID;

import static cn.lunadeer.lagrangeMC.protocols.MessageSegment.*;

public class PlayerInfo extends BotCommand {

    private final String template = "user_info";

    public PlayerInfo() {
        super("info", "查看个人信息", false, false, true);
    }

    @Override
    public void handle(long userId, String commandText, JSONObject jsonObject) {
        UUID uuid;
        String lastKnownName;
        String status;
        try {
            uuid = WhitelistTable.getInstance().getUserUUID(userId);
            lastKnownName = WhitelistTable.getInstance().getLastKnownName(uuid);
            Player player = LagrangeMC.getInstance().getServer().getOnlinePlayers().stream()
                    .filter(p -> p.getUniqueId().equals(uuid))
                    .findFirst()
                    .orElse(null);
            if (player == null) {
                status = "[上次在线] " + WhitelistTable.getInstance().getLastJoinAt(uuid);
            } else {
                status = "[当前在线]";
            }
        } catch (Exception e) {
            XLogger.error(e);
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!jsonObject.containsKey("message_id")) return;
        long messageID = jsonObject.getLong("message_id");

        try (TemplateFactory userInfo = new TemplateFactory(template)) {
            userInfo.setPlaceholder("nickname", lastKnownName)
                    .setPlaceholder("status", status)
                    .setPlaceholder("avatar", "https://api.mineatar.io/face/" + uuid + "?scale=12");    // get image from api https://api.mineatar.io/face/<UUID>
            BufferedImage userInfoImage = WebDriverManager.getInstance().takeScreenshot(userInfo.build(offlinePlayer), template);
            if (jsonObject.containsKey("group_id")) {
                long groupID = jsonObject.getLong("group_id");
                GroupOperation.SendGroupMessage(groupID, ReplySegment(messageID), userInfoImage != null ? ImageSegment(userInfoImage) : TextSegment("出错了，请联系管理员"));
            } else {
                PrivateOperation.SendPrivateMessage(userId, ReplySegment(messageID), userInfoImage != null ? ImageSegment(userInfoImage) : TextSegment("出错了，请联系管理员"));
            }
        } catch (Exception e) {
            XLogger.error("Failed to generate user info image");
            XLogger.error(e);
        }
    }

}
