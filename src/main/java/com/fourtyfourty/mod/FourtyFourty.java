package com.fourtyfourty.mod;

import com.fourtyfourty.mod.Reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.model.obj.OBJLoader;

import org.lwjgl.input.Keyboard;

import static com.fourtyfourty.mod.Reference.Reference.MODID;


@Mod(modid = MODID, useMetadata = true, canBeDeactivated = true, clientSideOnly = true)
public class FourtyFourty {
	private KeyBinding keys = new KeyBinding("Auto-FLy trigger key", Keyboard.KEY_U, "key.categories.misc");

	private Vec3d previousPosition;
	boolean isDescending;
	boolean pullUp;
	private boolean autoFlight;
	private float currentPitch = 0;


	@Instance(MODID)
	public static FourtyFourty instance;


	@EventHandler
	public void load(FMLInitializationEvent event) {
		reset();

		ClientRegistry.registerKeyBinding(keys);
			MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		String configDir = event.getModConfigurationDirectory().toString();
		if (event.getSide() == Side.CLIENT) {
			OBJLoader.INSTANCE.addDomain(Reference.MODID);
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
			if (org.lwjgl.input.Keyboard.isKeyDown(keys.getKeyCode())) {
				autoFlight = !autoFlight;
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Auto-Flight: " + autoFlight));
				reset();
			}
		}
	}


	@SubscribeEvent
	public void onAutoFly(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null)
			return;
		if(player.isElytraFlying() && autoFlight) {
			double velocity = calculateVelocity(player);
			if(velocity == 0)
				return;
			if(currentPitch == 0)
				currentPitch = player.rotationPitch;
			if(isDescending) {
				pullUp = false;
				if (velocity >= ModConfig.pullDownMaxVelocity) {
					isDescending = false;
					pullUp = true;
				}
			}

			else {
				pullUp = true;
				if (velocity <= ModConfig.pullUpMinVelocity) {
					isDescending = true;
					pullUp = false;
				}
			}
			if(pullUp) {
				currentPitch -= ModConfig.pullUpSpeed;
				if (currentPitch <= ModConfig.pullUpAngle)
					currentPitch = ModConfig.pullUpAngle;
			}
			else {
				currentPitch += ModConfig.pullDownSpeed;
				if (currentPitch >= ModConfig.pullDownAngle)
					currentPitch = ModConfig.pullDownAngle;
			}
			player.rotationPitch = currentPitch;
		}
		else
			reset();
	}

	private double calculateVelocity(EntityPlayer player)
	{
		Vec3d newPosition = player.getPositionVector();

		if (previousPosition == null)
			previousPosition = newPosition;

		Vec3d difference = new Vec3d(newPosition.x - previousPosition.x, newPosition.y - previousPosition.y, newPosition.z - previousPosition.z);

		previousPosition = newPosition;

		return difference.lengthVector();
	}

	private void reset() {
		isDescending = true;
		pullUp = false;
		currentPitch = 0;
	}
}
