package org.meanworks.render.geometry.animation;

import java.util.LinkedList;

import org.lwjgl.util.vector.Matrix4f;

public class Skeleton {

	/*
	 * The bones of this skeleton
	 */
	private Bone[] bones;

	/*
	 * We want this root bone so we can keep track of childrends This is a
	 * temporary solution, but it may become permanent
	 */
	private Bone rootBone;

	/**
	 * Construct a new skeleton
	 */
	public Skeleton() {

	}

	/**
	 * Recursively calculate the bone transforms By stepping down into the
	 * hierarchy
	 */
	public Matrix4f[] calculateGlobalTransforms() {
		Matrix4f[] globTransforms = new Matrix4f[bones.length];
		for (Bone b : rootBone.getChildren()) {
			recurCalculateGlobalTransforms(globTransforms, b);
		}
		return globTransforms;
	}

	/**
	 * Recursive method for calculateGlobalTransforms
	 * 
	 * @param b
	 */
	private void recurCalculateGlobalTransforms(Matrix4f[] globTransforms,
			Bone b) {
		globTransforms[b.getBoneId()] = b.calculateGlobalTransform_new();
		for (Bone child : b.getChildren()) {
			recurCalculateGlobalTransforms(globTransforms, child);
		}
	}

	/**
	 * Build the hierarchy for this skeleton Basically it gives each bone a id,
	 * which is used for copying
	 */
	public void buildHierarchy() {
		for (int i = 0; i < bones.length; i++) {
			bones[i].setBoneId(i);
		}

		rootBone = new Bone("mw_RootBone");

		// Compute children
		// This is an expensive test, but it's only used on skeleton creation
		// So I will allow it for now
		LinkedList<Bone> childrenList = new LinkedList<>();

		for (int i = 0; i < bones.length; i++) {
			if (bones[i].getParent() == null) {
				childrenList.add(bones[i]);
			}
		}
		Bone[] children = new Bone[childrenList.size()];
		childrenList.toArray(children);
		rootBone.setChildren(children);

		// We now have the top bones, we need to do a recursive search for the
		// bones later down
		for (Bone b : childrenList) {
			recurBuildChildren(b);
		}
	}

	/**
	 * Recursively build the children parent association
	 * 
	 * @param b
	 */
	public void recurBuildChildren(Bone b) {
		LinkedList<Bone> childrenList = new LinkedList<>();

		for (int i = 0; i < bones.length; i++) {
			if (bones[i].getParent() == b) {
				childrenList.add(bones[i]);
			}
		}

		Bone[] children = new Bone[childrenList.size()];
		childrenList.toArray(children);
		b.setChildren(children);

		for (Bone child : childrenList) {
			recurBuildChildren(child);
		}
	}

	/**
	 * Copy the other skeleton
	 * 
	 * @param other
	 *            The skeleton to copy
	 */
	public Skeleton(Skeleton other) {
		bones = new Bone[other.getBones().length];
		for (int i = 0; i < bones.length; i++) {
			bones[i] = new Bone(other.getBones()[i]);
		}
		for (int i = 0; i < bones.length; i++) {
			final int parentBoneId = other.getBones()[i].getParent() == null ? -1
					: other.getBones()[i].getParent().getBoneId();
			if (parentBoneId != -1) {
				bones[i].setParent(bones[parentBoneId]);
			}
		}
		buildHierarchy();
	}

	/**
	 * Find the bone with the given name
	 * 
	 * @param boneName
	 * @return
	 */
	public Bone findBone(String boneName) {
		for (int i = 0; i < bones.length; i++) {
			if (bones[i].getBoneName().equals(boneName)) {
				return bones[i];
			}
		}
		return null;
	}

	/**
	 * Get the bones of this skeleton
	 * 
	 * @return
	 */
	public Bone[] getBones() {
		return bones;
	}

	/**
	 * Set the bones of this skeleton
	 * 
	 * @param bones
	 */
	public void setBones(Bone[] bones) {
		this.bones = bones;
		buildHierarchy();
	}

}
