package twilightforest.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFHydraSpawner;
import twilightforest.tileentity.TileEntityTFKnightPhantomsSpawner;
import twilightforest.tileentity.TileEntityTFLichSpawner;
import twilightforest.tileentity.TileEntityTFNagaSpawner;
import twilightforest.tileentity.TileEntityTFSnowQueenSpawner;
import twilightforest.tileentity.TileEntityTFTowerBossSpawner;

public class BlockTFBossSpawner extends BlockContainer {

    protected BlockTFBossSpawner() {
        super(Material.rock);
        this.setHardness(20F);
        // this.setResistance(10F);
        // this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer Moving this to
     * the Block base class allows for mods that wish to extend vanilla blocks, and also want to have a
     * tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    /**
     * This is where we actually give out our tile entity
     */
    @Override
    public TileEntity createTileEntity(World world, int meta) {
        switch (meta) {
        case 0:
            return new TileEntityTFNagaSpawner();
        case 1:
            return new TileEntityTFLichSpawner();
        case 2:
            return new TileEntityTFHydraSpawner();
        case 3:
            return new TileEntityTFTowerBossSpawner();
        case 4:
            return new TileEntityTFKnightPhantomsSpawner();
        case 5:
            return new TileEntityTFSnowQueenSpawner();
        default:
            return null;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return createTileEntity(var1, var2);
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return null;
    }

    /**
     * quantity dropped
     */
    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
         list.add(new ItemStack(item, 1, 0));
         list.add(new ItemStack(item, 1, 1));
         list.add(new ItemStack(item, 1, 2));
         list.add(new ItemStack(item, 1, 3));
         list.add(new ItemStack(item, 1, 4));
         list.add(new ItemStack(item, 1, 5));
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int side, int metadata) {
        return Blocks.mob_spawner.getIcon(side, metadata);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        ; // don't load anything
    }
}
