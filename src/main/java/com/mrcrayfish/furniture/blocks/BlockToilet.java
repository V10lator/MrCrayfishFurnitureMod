/**
 * MrCrayfish's Furniture Mod
 * Copyright (C) 2016  MrCrayfish (http://www.mrcrayfish.com/)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mrcrayfish.furniture.blocks;

import com.mrcrayfish.furniture.advancement.Triggers;
import com.mrcrayfish.furniture.entity.EntitySittableBlock;
import com.mrcrayfish.furniture.handler.InputHandler;
import com.mrcrayfish.furniture.init.FurnitureSounds;
import com.mrcrayfish.furniture.util.CollisionHelper;
import com.mrcrayfish.furniture.util.SittableUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BlockToilet extends BlockFurniture
{
    private static final AxisAlignedBB BOUNDING_BOX_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, 0.1, 0.0, 0.15, 1.0, 1.0, 0.85);
    private static final AxisAlignedBB BOUNDING_BOX_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, 0.1, 0.0, 0.15, 1.0, 1.0, 0.85);
    private static final AxisAlignedBB BOUNDING_BOX_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, 0.1, 0.0, 0.15, 1.0, 1.0, 0.85);
    private static final AxisAlignedBB BOUNDING_BOX_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, 0.1, 0.0, 0.15, 1.0, 1.0, 0.85);
    private static final AxisAlignedBB[] BOUNDING_BOX = {BOUNDING_BOX_SOUTH, BOUNDING_BOX_WEST, BOUNDING_BOX_NORTH, BOUNDING_BOX_EAST};

    private static final AxisAlignedBB COLLISION_BOX_TANK_NORTH = CollisionHelper.getBlockBounds(EnumFacing.NORTH, 0.65, 0.5, 0.0, 1.0, 1.1, 1.0);
    private static final AxisAlignedBB COLLISION_BOX_TANK_EAST = CollisionHelper.getBlockBounds(EnumFacing.EAST, 0.65, 0.5, 0.0, 1.0, 1.1, 1.0);
    private static final AxisAlignedBB COLLISION_BOX_TANK_SOUTH = CollisionHelper.getBlockBounds(EnumFacing.SOUTH, 0.65, 0.5, 0.0, 1.0, 1.1, 1.0);
    private static final AxisAlignedBB COLLISION_BOX_TANK_WEST = CollisionHelper.getBlockBounds(EnumFacing.WEST, 0.65, 0.5, 0.0, 1.0, 1.1, 1.0);
    private static final AxisAlignedBB[] COLLISION_BOX_TANK = {COLLISION_BOX_TANK_SOUTH, COLLISION_BOX_TANK_WEST, COLLISION_BOX_TANK_NORTH, COLLISION_BOX_TANK_EAST};

    private static final AxisAlignedBB COLLISION_BOX_SEAT = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);

    public BlockToilet(Material material)
    {
        super(material);
        this.setHardness(1.0F);
        this.setSoundType(SoundType.STONE);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if(placer instanceof EntityPlayer)
        {
            Triggers.trigger(Triggers.PLACE_BATHTROOM_FURNITURE, (EntityPlayer) placer);
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!playerIn.isSneaking())
        {
            if(SittableUtil.sitOnBlockWithRotationOffset(worldIn, pos.getX(), pos.getY(), pos.getZ(), playerIn, 0.4D, getMetaFromState(state), 0.1D))
            {
                if(worldIn.isRemote)
                    playerIn.sendMessage(new TextComponentString(TextFormatting.YELLOW + I18n.format("cfm.message.toilet", Keyboard.getKeyName(InputHandler.KEY_FART.getKeyCode()))));
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        else
        {
            List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D).expand(1D, 1D, 1D));
            for(EntityItem item : items)
            {
                item.setDead();
            }
            worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, FurnitureSounds.flush, SoundCategory.BLOCKS, 0.75F, 1.0F, false);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing facing = state.getValue(FACING);
        return BOUNDING_BOX[facing.getHorizontalIndex()];
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_)
    {
        if(!(entityIn instanceof EntitySittableBlock))
        {
            EnumFacing facing = state.getValue(FACING);
            super.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_TANK[facing.getHorizontalIndex()]);
            super.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOX_SEAT);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return SittableUtil.isSomeoneSitting(worldIn, pos.getX(), pos.getY(), pos.getZ()) ? 1 : 0;
    }
}
