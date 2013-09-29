package org.fractalstudio.render.geometry.animation;

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
