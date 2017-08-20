// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.*;

// Referenced classes of package net.minecraft.src:
//            IChunkLoader, RegionFileCache, CompressedStreamTools, NBTTagCompound, 
//            ChunkLoader, Chunk, World, WorldInfo

public class McRegionChunkLoader
    implements IChunkLoader
{

    public McRegionChunkLoader(File file)
    {
        worldFolder = file;
    }

    public Chunk loadChunk(World world, int i, int j)
        throws IOException
    {
        java.io.DataInputStream datainputstream = RegionFileCache.func_22124_c(worldFolder, i, j);
        NBTTagCompound nbttagcompound;
        if(datainputstream != null)
        {
            nbttagcompound = CompressedStreamTools.func_774_a(datainputstream);
        } else
        {
            return null;
        }
        if(!nbttagcompound.hasKey("Level"))
        {
            System.out.println((new StringBuilder()).append("Chunk file at ").append(i).append(",").append(j).append(" is missing level data, skipping").toString());
            return null;
        }
        if(!nbttagcompound.getCompoundTag("Level").hasKey("Blocks"))
        {
            System.out.println((new StringBuilder()).append("Chunk file at ").append(i).append(",").append(j).append(" is missing block data, skipping").toString());
            return null;
        }
        Chunk chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"));
        if(!chunk.isAtLocation(i, j))
        {
            System.out.println((new StringBuilder()).append("Chunk file at ").append(i).append(",").append(j).append(" is in the wrong location; relocating. (Expected ").append(i).append(", ").append(j).append(", got ").append(chunk.xPosition).append(", ").append(chunk.zPosition).append(")").toString());
            nbttagcompound.setInteger("xPos", i);
            nbttagcompound.setInteger("zPos", j);
            chunk = ChunkLoader.loadChunkIntoWorldFromCompound(world, nbttagcompound.getCompoundTag("Level"));
        }
        chunk.func_25083_h();
        return chunk;
    }

    public void saveChunk(World world, Chunk chunk)
        throws IOException
    {
        world.checkSessionLock();
        try
        {
            DataOutputStream dataoutputstream = RegionFileCache.func_22120_d(worldFolder, chunk.xPosition, chunk.zPosition);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            ChunkLoader.storeChunkInCompound(chunk, world, nbttagcompound1);
            CompressedStreamTools.func_771_a(nbttagcompound, dataoutputstream);
            dataoutputstream.close();
            WorldInfo worldinfo = world.getWorldInfo();
            worldinfo.setSizeOnDisk(worldinfo.getSizeOnDisk() + (long)RegionFileCache.func_22121_b(worldFolder, chunk.xPosition, chunk.zPosition));
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void saveExtraChunkData(World world, Chunk chunk)
        throws IOException
    {
    }

    public void func_661_a()
    {
    }

    public void saveExtraData()
    {
    }

    private final File worldFolder;
}
