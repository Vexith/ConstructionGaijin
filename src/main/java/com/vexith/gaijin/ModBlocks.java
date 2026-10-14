package com.vexith.gaijin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;


public class ModBlocks {
    public static final Block CONCRETE_BOARD = registerBlock("concrete_board", new PlankFormBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    public static final Block WET_CONCRETE = registerBlock("wet_concrete",
            new WetConcreteBlock(BlockBehaviour.Properties.copy(Blocks.WATER)
                    .mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.SLIME_BLOCK)
                    .randomTicks() 
                    .strength(0.5f).noCollission()));

   
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(ConstructionGaijin.MOD_ID, name), block);    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(ConstructionGaijin.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));    
     }

    public static void registerModBlocks() {
        System.out.println("Registering files for blocks");
    }
}

class PlankFormBlock extends DirectionalBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public PlankFormBlock(Properties properties){
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override 
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override 
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case DOWN: return Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
            case UP: return Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
            case NORTH: return Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
            case SOUTH: return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
            case WEST: return Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            case EAST: return Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
            default: return Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        }
    }

    @Override 
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }
}

class WetConcreteBlock extends Block {
    public static final IntegerProperty CURING_STAGE = IntegerProperty.create("curing_stage", 0, 3);

    public WetConcreteBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CURING_STAGE, 0));
    }

    @Override 
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(CURING_STAGE);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int currentStage = state.getValue(CURING_STAGE);

        if (currentStage < 3) {
            world.setBlock(pos, state.setValue(CURING_STAGE, currentStage + 1), 3);
        } else {
            world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
        }
    }
}