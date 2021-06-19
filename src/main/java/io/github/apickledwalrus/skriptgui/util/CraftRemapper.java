package io.github.apickledwalrus.skriptgui.util;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Kenzie (https://github.com/Moderocky/)
 */
public class CraftRemapper {

	public static Class<?> remapGUIExtension(final String nmsVersion, final ClassLoader loader) {
		final String superName = "org/bukkit/craftbukkit/" + nmsVersion + "inventory/CraftInventory";
		final String internalName = "io/github/apickledwalrus/skriptgui/gui/LinkingCraftGUI";
		final String nmsInventory;
		if (Skript.getMinecraftVersion().isSmallerThan(new Version(1, 17))) { // 1.16.5 and below (old mappings)
			nmsInventory = "net/minecraft/server/" + nmsVersion + "IInventory";
		} else { // 1.17 and above (new mappings)
			nmsInventory = "net/minecraft/world/IInventory";
		}
		final ClassWriter writer = new ClassWriter(ASM5);
		writer.visit(V1_8, ACC_PUBLIC | ACC_SUPER, internalName, null, superName, null);
		final MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/bukkit/inventory/Inventory;)V", null, null);
		constructor.visitCode();
		constructor.visitVarInsn(ALOAD, 0);
		constructor.visitVarInsn(ALOAD, 1);
		constructor.visitTypeInsn(CHECKCAST, superName);
		constructor.visitMethodInsn(INVOKEVIRTUAL, superName, "getInventory", "()L" + nmsInventory + ";", false);
		constructor.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "(L" + nmsInventory + ";)V", false);
		constructor.visitInsn(RETURN);
		constructor.visitMaxs(2, 2);
		constructor.visitEnd();
		writer.visitEnd();
		final byte[] bytecode = writer.toByteArray();
		try {
			remap_field: try {
				final Field field = Class.class.getDeclaredField("module");
				final Unsafe unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
					final Field the = Unsafe.class.getDeclaredField("theUnsafe");
					the.setAccessible(true);
					return (Unsafe) the.get(null);
				});
				final long offset = unsafe.objectFieldOffset(field);
				unsafe.putObject(CraftRemapper.class, offset, unsafe.getObject(Object.class, offset));
			} catch (NoSuchFieldException ex) {
				break remap_field; // Java 8
			}
			final Method define = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			define.setAccessible(true);
			final Class<?> cls = (Class<?>) define.invoke(loader, internalName.replace("/", "."), bytecode, 0, bytecode.length);
			cls.getName(); // Assert class is loaded into memory
			return cls;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
