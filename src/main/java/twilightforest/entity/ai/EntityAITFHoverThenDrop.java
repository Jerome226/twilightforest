package twilightforest.entity.ai;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import twilightforest.entity.boss.EntityTFSnowQueen;
import twilightforest.entity.boss.EntityTFSnowQueen.Phase;

public class EntityAITFHoverThenDrop extends EntityAIBase {

    private static final float HOVER_HEIGHT = 6F;
    private static final float HOVER_RADIUS = 0;
    private Class<? extends EntityLivingBase> classTarget;
    private EntityTFSnowQueen attacker;

    private double hoverPosX;
    private double hoverPosY;
    private double hoverPosZ;
    private int hoverTimer;
    private int dropTimer;
    private int maxHoverTime;
    private int maxDropTime;
    private int seekTimer;
    private int maxSeekTime;

    private double dropY;

    public EntityAITFHoverThenDrop(EntityTFSnowQueen entityTFSnowQueen, Class<EntityPlayer> class1, int hoverTime, int dropTime) {
        this.attacker = entityTFSnowQueen;
        this.classTarget = class1;
        this.setMutexBits(3);
        this.maxHoverTime = hoverTime;
        this.maxSeekTime = hoverTime;
        this.maxDropTime = dropTime;

        this.hoverTimer = 0;

    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();

        if (target == null) {
            return false;
        } else if (!target.isEntityAlive()) {
            return false;
        } else if (this.classTarget != null && !this.classTarget.isAssignableFrom(target.getClass())) {
            return false;
        } else if (this.attacker.getCurrentPhase() != Phase.DROP) {
            return false;
        } else {
            // System.out.println("We can hover at target!");

            return true;// attacker.canEntityBeSeen(target);
        }

    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();

        if (target == null || !target.isEntityAlive()) {
            return false;
        } else if (this.attacker.getCurrentPhase() != Phase.DROP) {
            return false;
        } else if (this.seekTimer > this.maxSeekTime) {
            return false;
        } else if (this.attacker.getDistanceSq(hoverPosX, hoverPosY, hoverPosZ) <= 1.0F) {
            // are we there yet?
            // System.out.println("Successfully in position, beginning drop mode");
            this.hoverTimer++;
            return true;
        } else if (this.dropTimer < this.maxDropTime) {
            return true;
        } else {
            // max drop time!
            this.attacker.incrementSuccessfulDrops();
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        EntityLivingBase target = this.attacker.getAttackTarget();

        if (target != null) {
            // find a spot above the player
            makeNewHoverSpot(target);
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.hoverTimer = 0;
        this.dropTimer = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {

        // if we have hit the drop spot, start dropping
        if (this.hoverTimer > 0) {
            this.hoverTimer++;
        } else {
            this.seekTimer++;
        }

        if (this.hoverTimer < this.maxHoverTime) {

            // check if we are at our waypoint target
            double offsetX = this.hoverPosX - this.attacker.posX;
            double offsetY = this.hoverPosY - this.attacker.posY;
            double offsetZ = this.hoverPosZ - this.attacker.posZ;

            double distanceDesired = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;

            distanceDesired = (double) MathHelper.sqrt_double(distanceDesired);

            // add velocity
            double velX = offsetX / distanceDesired * 0.05D;
            double velY = offsetY / distanceDesired * 0.1D;
            double velZ = offsetZ / distanceDesired * 0.05D;

            // gravity offset
            velY += 0.05F;

            this.attacker.addVelocity(velX, velY, velZ);

            // look at target
            EntityLivingBase target = this.attacker.getAttackTarget();
            if (target != null) {
                this.attacker.faceEntity(target, 30.0F, 30.0F);
                this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }

            // System.out.println("Hovering!");
        } else {
            // drop!
            // System.out.println("Dropping!");
            this.dropTimer++;

            if (this.attacker.posY > this.dropY) {
                this.attacker.destroyBlocksInAABB(this.attacker.boundingBox.expand(1, 0.5F, 1));
            }
        }
    }

    /**
     * Make a new spot to hover at!
     */
    private void makeNewHoverSpot(EntityLivingBase target) {
        double hx = 0, hy = 0, hz = 0;

        int tries = 100;

        for (int i = 0; i < tries; i++) {
            hx = target.posX + (this.attacker.getRNG().nextFloat() - this.attacker.getRNG().nextFloat()) * HOVER_RADIUS;
            hy = target.posY + HOVER_HEIGHT;
            hz = target.posZ + (this.attacker.getRNG().nextFloat() - this.attacker.getRNG().nextFloat()) * HOVER_RADIUS;

            if (!isPositionOccupied(hx, hy, hz) && this.canEntitySee(this.attacker, hx, hy, hz) && this.canEntitySee(target, hx, hy, hz)) {
                break;
            }
        }

        this.hoverPosX = hx;
        this.hoverPosY = hy;
        this.hoverPosZ = hz;

        this.dropY = target.posY - 1F;
        this.seekTimer = 0;
    }

    private boolean isPositionOccupied(double hx, double hy, double hz) {
        float radius = this.attacker.width / 2F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(hx - radius, hy, hz - radius, hx + radius, hy + this.attacker.height, hz + radius);

        boolean isOccupied = this.attacker.worldObj.getCollidingBoundingBoxes(attacker, aabb).isEmpty();

        return isOccupied;
    }

    /**
     * Can the specified entity see the specified location?
     */
    protected boolean canEntitySee(Entity entity, double dx, double dy, double dz) {
        return entity.worldObj.rayTraceBlocks(Vec3.createVectorHelper(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ), Vec3.createVectorHelper(dx, dy, dz)) == null;

    }
}
