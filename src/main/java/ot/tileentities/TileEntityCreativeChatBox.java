package ot.tileentities;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.ForgeHooks;
import ot.system.ChatBoxEventSystem;

import java.util.List;

/**
 * Created by Avaja on 05.05.2016.
 */
public class TileEntityCreativeChatBox extends TileEntity implements Analyzable, Environment {
    protected Node node;
    private boolean addToNetwork = false;

    public TileEntityCreativeChatBox() {
        node = li.cil.oc.api.Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();
        ChatBoxEventSystem.add(this);
    }

    @Override
    public void updateEntity() {
        if (!addToNetwork){
            addToNetwork = true;
            li.cil.oc.api.Network.joinOrCreateNetwork(this);
        }
    }


    public String getComponentName() {
        return "admin_chatbox";
    }

    public void eventMessage(EntityPlayer player, String message){
        if(node != null)
            node.sendToReachable("computer.signal", "chat_message", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName(), message);

    }

    public void eventCommand(EntityPlayer player, String message){
        if(node != null)
            node.sendToReachable("computer.signal", "chat_command", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName(), message);

    }

    public void eventDeath(EntityPlayer player, DamageSource damageSource){
        if(node != null){
            if(damageSource instanceof EntityDamageSource){
                EntityDamageSource entityDamageSource = (EntityDamageSource) damageSource;
                String damage = "";
                try{
                    damage = entityDamageSource.func_151519_b(player).getUnformattedTextForChat();
                }catch (Exception e){
                    e.printStackTrace();
                }
                node.sendToReachable("computer.signal", "player_death", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName(), damageSource.damageType, damage);
            }else{

                node.sendToReachable("computer.signal", "player_death", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName(), damageSource.damageType, null);
            }
        }
    }

    public void eventLoggedIn(EntityPlayer player){
        if(node != null)
            node.sendToReachable("computer.signal", "player_join", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName());

    }


    public void eventLoggedOut(EntityPlayer player) {
        if(node != null)
            node.sendToReachable("computer.signal", "player_quit", player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, getDistanceFrom(player.posX, player.posY, player.posZ), player.getDisplayName());
    }

    @Override
    public Node[] onAnalyze(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return new Node[]{node};
    }

    @Callback(doc="function(message:string); say some text")
    public Object[] say(Context context, Arguments arguments) throws Exception{

        System.out.println(String.format("say: x=%d, y=%d, z=%d", xCoord, yCoord, zCoord));

        List<EntityPlayer> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayer player : players){
            player.addChatMessage(ForgeHooks.newChatWithLinks(arguments.checkString(0)));
        }
        return new Object[]{};
    }

    @Callback(doc="function(name:string, message:string); ")
    public Object[] tell(Context context, Arguments arguments) throws Exception{
        String name = arguments.checkString(0);
        String message = arguments.checkString(1);

        List<EntityPlayer> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayer player : players){
            if (player.getDisplayName().equals(name))
                player.addChatMessage(ForgeHooks.newChatWithLinks(message));
        }
        return new Object[]{};
    }

    @Override
    public Node node() {
        return node;
    }

    @Override
    public void onConnect(Node node) {

    }

    @Override
    public void onDisconnect(Node node) {

    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (node != null) node.remove();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (node != null) node.remove();
        ChatBoxEventSystem.remove(this);
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (node != null && node.host() == this) {
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
    }
}
