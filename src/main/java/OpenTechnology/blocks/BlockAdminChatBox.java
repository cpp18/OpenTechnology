package OpenTechnology.blocks;

import OpenTechnology.OpenTechnology;
import OpenTechnology.system.ChatBoxEventSystem;
import OpenTechnology.tileentities.TileEntityAdminChatBox;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Avaja on 05.05.2016.
 */
public class BlockAdminChatBox extends BlockContainer {

    public BlockAdminChatBox() {
        super(Material.iron);
        this.setBlockName("OpenTechnology_adminchatbox");
        this.setBlockTextureName(OpenTechnology.MODID+":adminchatbox");
        this.setCreativeTab(OpenTechnology.tab);
        setHarvestLevel("pickaxe", 0);
        setHardness(5);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i1) {
        return new TileEntityAdminChatBox();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
        TileEntityAdminChatBox box = (TileEntityAdminChatBox) world.getTileEntity(x, y, z);
        ChatBoxEventSystem.remove(box);

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }
}
