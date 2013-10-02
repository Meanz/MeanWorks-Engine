package org.meanworks.render.geometry.animation;

public class Skeleton {

	/*
	 * The bones of this skeleton
	 */
	private Bone[] bones;

	/**
	 * Construct a new skeleton
	 */
	public Skeleton() {

	}

	/**
	 * Copy the other skeleton
	 */
	public Skeleton(Skeleton other) {
		bones = new Bone[other.getBones().length];
		for (int i = 0; i < bones.length; i++) {
			bones[i] = new Bone(other.getBones()[i]);
		}
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
	}

}
