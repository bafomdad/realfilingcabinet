package com.bafomdad.realfilingcabinet.blocks;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IFolder;
import com.bafomdad.realfilingcabinet.blocks.entity.FilingCabinetEntity;
import com.bafomdad.realfilingcabinet.utils.StorageUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class FilingCabinetBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;
    static float f = 0.0625F;
    protected static final VoxelShape BASE_AABB = VoxelShapes.cube(0.0D + f, 0.0D, 0.0D + f, 1.0D - f, 1.0D - f, 1.0D - f);

    public FilingCabinetBlock(Settings settings) {

        super(settings);
        setDefaultState(stateFactory.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> st) {

        st.with(FACING);
    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FilingCabinetEntity)
            leftClick((FilingCabinetEntity)be, player);

        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {

        if (hand == Hand.MAIN) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FilingCabinetEntity)
                rightClick((FilingCabinetEntity)be, player);
            return true;
        }
        return false;
    }

    private void leftClick(FilingCabinetEntity be, PlayerEntity player) {

        if (player.isCreative()) return;

        StorageUtils.extractStackManually(be, player);
//        if (!player.world.isRemote)
//            RealFilingCabinet.addons.forEach(r -> r.register());
    }

    private void rightClick(FilingCabinetEntity be, PlayerEntity player) {

        ItemStack stack = player.getMainHandStack();
        if (!player.isSneaking() && !stack.isEmpty()) {
            if (stack.getItem() instanceof IFolder && be.isOpen) {
                for (int i = 0; i < be.getInvSize(); i++) {
                    ItemStack loopStack = be.getInventory().get(i);
                    if (loopStack.isEmpty()) {
                        be.getInventory().set(i, stack);
                        player.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
                        be.markBlockForUpdate();
                        return;
                    }
                }
            }
            else if (!(stack.getItem() instanceof IFolder)) {
                StorageUtils.addStackManually(be, player, stack);
            }
        }
        if (!player.isSneaking() && stack.isEmpty()) {
            if (!be.getWorld().isRemote) {
                be.isOpen = !be.isOpen;
            }
            be.markBlockForUpdate();
        }
        if (player.isSneaking() && stack.isEmpty() && be.isOpen) {
            StorageUtils.folderExtract(be, player);
        }
    }

    /**
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

        if (entity.isSneaking() && entity instanceof PlayerEntity) {
            System.out.println("oi " + entity.getEntityName() + ", get the hell away from me");
        }
    }
    */

    @Override
    public VoxelShape getBoundingShape(BlockState state, BlockView world, BlockPos pos) {

        return BASE_AABB;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {

        return this.getDefaultState().with(FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }

    @Override
    public RenderTypeBlock getRenderType(BlockState state) {

        return RenderTypeBlock.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {

        return new FilingCabinetEntity();
    }
}
