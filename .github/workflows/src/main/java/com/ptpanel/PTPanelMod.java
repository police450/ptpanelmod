
package com.ptpanel;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Mod("ptpanelmod")
public class PTPanelMod {

    public PTPanelMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onScreenOpen(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof JoinMultiplayerScreen)) return;

        try {
            String panelUrl = "https://panel.vipercraft.org";
            String apiKey = "ptlc_rM86psk0gKmwkzV3dTRMQAP6rlIH0XsjVGex8k7kYDQ";

            URL url = new URL(panelUrl + "/api/client");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            JsonObject response = JsonParser.parseReader(in).getAsJsonObject();
            in.close();

            JsonArray serversArray = response.getAsJsonArray("data");

            for (JsonElement elem : serversArray) {
                JsonObject attributes = elem.getAsJsonObject().getAsJsonObject("attributes");
                String name = attributes.get("name").getAsString();
                String identifier = attributes.get("identifier").getAsString();

                // Fallback: use fake address since full server detail fetch is skipped here
                ServerData serverData = new ServerData("[PTPanel] " + name, "127.0.0.1:25565", false);
                Minecraft.getInstance().getCurrentServerList().add(serverData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
