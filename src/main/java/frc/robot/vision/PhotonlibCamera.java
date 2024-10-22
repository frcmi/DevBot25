package frc.robot.vision;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;

public final class PhotonlibCamera implements Camera {
    private String m_Name;
    private Transform3d m_Offset;

    private PhotonCamera m_Camera;
    private PhotonPoseEstimator m_Estimator;

    public PhotonlibCamera(String name, Transform3d offset, AprilTagFieldLayout layout) {
        m_Name = name;
        m_Offset = offset;

        m_Camera = new PhotonCamera(name);
        m_Estimator = new PhotonPoseEstimator(layout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, m_Camera, offset);
    }

    @Override
    public void update(Result result) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void setReference(Pose2d pose) {
        m_Estimator.setReferencePose(pose);
    }

    @Override
    public String getName() { return m_Name; }

    @Override
    public Transform3d getOffset() { return m_Offset; }

    @Override
    public Simulator createSimulator(Specification specification) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSimulator'");
    }
    
}
