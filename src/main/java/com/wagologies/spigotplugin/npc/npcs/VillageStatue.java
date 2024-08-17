package com.wagologies.spigotplugin.npc.npcs;

import com.wagologies.spigotplugin.SpigotPlugin;
import com.wagologies.spigotplugin.campaign.Campaign;
import com.wagologies.spigotplugin.event.player.RPGClickNPCEvent;
import com.wagologies.spigotplugin.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

public class VillageStatue extends NPC {
    public VillageStatue(SpigotPlugin plugin, Campaign campaign) {
        super(plugin, campaign);
        setTargetLocation(new Location(campaign.getWorld(), 561.5, 119,801.5, 180, 0));
    }

    @Override
    protected void setupNPC() {
        SkinTrait skinTrait = citizenNPC.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent("VillageStatue", getSkinSignature(), getSkinTexture());
        citizenNPC.removeTrait(LookClose.class);
        citizenNPC.removeTrait(HologramTrait.class);
    }

    @Override
    public String getName() {
        return "Village Statue";
    }

    @Override
    public void onInteract(RPGClickNPCEvent event) {

    }

    public String getSkinSignature() {
        return "BccG8fhclsFTclMsj+3TdE78gmNyi1RKOJmPhO8aXgZnBJAbcxJRaV9tpGUiqBsT2H2TnaJxmnPN1ME/K6+t+HBxJYTYHOV87zPRqH+e4DuHMNtD9cbDsscm+89sEW5JkE/njTF4jWDPhT+mgBALkOFxNKmH46OkQjBhXPMzVaXSHr5KGy8SlWuLK/YhC+mwhSqTIe8xm5/D6Uk8PYUE4f6hdn8pDyJUq2AT+lJ0kVL07fSYX5mQOAs+cIhXwbVSQ6k4IwppAma3RiXyOHUYNmSIuG8LSppbwx18uxnllw7HiKXfXQSlMll3gHXmjghQVrXGaKVtnxtLe6G1YOsGCnyk6HuFX9S4uhezFXWjgV5sWjhRi3AEHwZ/Og6LTqqLad+eCGnCN2EQcegvwAO83oX6lX11hJrfngMs/yH2lrj8pnBReUqd+kKM6AUGikd3gTmbpD8+15i/04rQdWZAkKBoBH7pWvjH5/khjWjyPQJTyteCssdH8bYYDITSMV1IAg0geUdog60jeFSZP22g4vygrhEyBjmc5R+iVGVDFzY6QFJI7miLY6z9rVFrGoF7pg9yINGtcVy9Dn3gtqTtY6l96GmyDmshwB19+B/eAcum0gwbAYjUQ7aB/2DZc40mmu3uXhxkOgWCn2AKAFnmqNXjN+PHjLKTE5PzNWxVxVI=";
    }

    public String getSkinTexture() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTYyNDQxMjAyNjg1NCwKICAicHJvZmlsZUlkIiA6ICIwYWFjMWRlZjUwZmI0N2RjODNmOGU2Njk3MTg1ODRkZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVhcGlpc2JhZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lOGYwMTUwYTZkZWE1NGFkZjgyNmI2NmEwMWVmZjM2YjE0ZGE4OTM4MjJiOGIwZGM4YTc2MGExYmEwNWM3ZGE2IgogICAgfQogIH0KfQ==";
    }
}
