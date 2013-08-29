package rajawali.materials.shaders.fragments.animation;

import rajawali.materials.plugins.VertexAnimationMaterialPlugin.VertexAnimationShaderVar;
import rajawali.materials.shaders.AShader;
import rajawali.materials.shaders.IShaderFragment;
import android.opengl.GLES20;

public class VertexAnimationVertexShaderFragment extends AShader implements IShaderFragment {

	public final static String SHADER_ID = "VERTEX_ANIMATION_VERTEX";

	private RVec4 maNextFramePosition;
	private RVec3 maNextFrameNormal;
	private RFloat muInterpolation;
	
	private int maNextFramePositionHandle;
	private int maNextFrameNormalHandle;
	private int muInterpolationHandle;

	public VertexAnimationVertexShaderFragment()
	{
		super(ShaderType.VERTEX_SHADER_FRAGMENT);
		initialize();
	}

	@Override
	public void initialize()
	{
		super.initialize();

		maNextFramePosition = (RVec4) addAttribute(VertexAnimationShaderVar.A_NEXT_FRAME_POSITION);
		maNextFrameNormal = (RVec3) addAttribute(VertexAnimationShaderVar.A_NEXT_FRAME_NORMAL);
		muInterpolation = (RFloat) addUniform(VertexAnimationShaderVar.U_INTERPOLATION);
	}

	public String getShaderId() {
		return SHADER_ID;
	}

	@Override
	public void main() {
		RVec4 position = (RVec4)getGlobal(DefaultShaderVar.G_POSITION);
		RVec3 normal = (RVec3)getGlobal(DefaultShaderVar.G_NORMAL);
		RVec4 aPosition = (RVec4)getGlobal(DefaultShaderVar.A_POSITION);
		RVec3 aNormal = (RVec3)getGlobal(DefaultShaderVar.A_NORMAL);
		
		//
		// -- position = aPosition + uInterpolation * (aNextFramePosition - aPosition);
		//
		position.assign(aPosition.add(muInterpolation.multiply(enclose(maNextFramePosition.subtract(aPosition)))));
		
		//
		// -- normal = aNormal + uInterpolation * (aNextFrameNormal - aNormal);
		//
		normal.assign(aNormal.add(muInterpolation.multiply(enclose(maNextFrameNormal.subtract(aNormal)))));
	}

	@Override
	public void setLocations(final int programHandle) {
		maNextFramePositionHandle = getAttribLocation(programHandle, VertexAnimationShaderVar.A_NEXT_FRAME_POSITION);
		maNextFrameNormalHandle = getAttribLocation(programHandle, VertexAnimationShaderVar.A_NEXT_FRAME_NORMAL);
		muInterpolationHandle = getUniformLocation(programHandle, VertexAnimationShaderVar.U_INTERPOLATION);
	}

	public void setNextFrameVertices(final int vertexBufferHandle)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferHandle);
		GLES20.glEnableVertexAttribArray(maNextFramePositionHandle);
		GLES20.glVertexAttribPointer(maNextFramePositionHandle, 3, GLES20.GL_FLOAT,
				false, 0, 0);
	}

	public void setNextFrameNormals(final int normalBufferHandle)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalBufferHandle);
		GLES20.glEnableVertexAttribArray(maNextFrameNormalHandle);
		GLES20.glVertexAttribPointer(maNextFrameNormalHandle, 3, GLES20.GL_FLOAT,
				false, 0, 0);
	}
	
	public void setInterpolation(double interpolation) {
		GLES20.glUniform1f(muInterpolationHandle, (float) interpolation);
	}
}
