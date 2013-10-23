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
	 * Build the hierarchy for this skeleton Basically it gives each bone a id,
	 * which is used for copying
	 */
	public void buildHierarchy() {
		for (int i = 0; i < bones.length; i++) {
			bones[i].setBoneId(i);
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
