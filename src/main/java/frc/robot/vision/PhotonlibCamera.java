package frc.robot.vision;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;

public final class PhotonlibCamera implements Camera {
    private Optional<Double> m_LastTimestamp;

    private String m_Name;
    private Transform3d m_Offset;

    private PhotonCamera m_Camera;
    private PhotonPoseEstimator m_Estimator;

    public PhotonlibCamera(String name, Transform3d offset, AprilTagFieldLayout layout) {
        m_LastTimestamp = Optional.empty();

        m_Name = name;
        m_Offset = offset;

        m_Camera = new PhotonCamera(name);
        m_Estimator = new PhotonPoseEstimator(layout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, offset);
    }

    private boolean isResultValid(PhotonPipelineResult result) {
        if (!result.hasTargets()) {
            return false;
        }

        for (var target : result.getTargets()) {
            int id = target.getFiducialId();
            if (id < 1 || id > 8) {
                return false;
            }
        }

        return true;
    }

    private void update(Result result, PhotonPipelineResult cameraResult) {
        Optional<EstimatedRobotPose> estimatedPose = isResultValid(cameraResult) ? m_Estimator.update(cameraResult)
                : Optional.empty();
        if (!estimatedPose.isPresent()) {
            return;
        }

        var pose = estimatedPose.get();
        if (m_LastTimestamp.isPresent() && pose.timestampSeconds - m_LastTimestamp.get() > Double.MIN_VALUE) {
            return;
        }

        var targets = cameraResult.getTargets();
        var firstTarget = targets.get(0);

        result.isNew = true;
        result.targetID = firstTarget.getFiducialId();
        result.pose = pose.estimatedPose.toPose2d();
        result.timestamp = pose.timestampSeconds;

        result.minDistance = Double.MAX_VALUE;
        result.maxDistance = Double.MIN_VALUE;
        result.maxAmbiguity = Double.MIN_VALUE;

        for (var target : targets) {
            var transform = target.getBestCameraToTarget();
            double distance = transform.getTranslation().getNorm();
            double ambiguity = target.getPoseAmbiguity();

            result.minDistance = Math.min(result.minDistance, distance);
            result.maxDistance = Math.max(result.maxDistance, distance);
            result.maxAmbiguity = Math.max(result.maxAmbiguity, ambiguity);
        }

        m_LastTimestamp = Optional.of(pose.timestampSeconds);
    }

    @Override
    public void update(Result result) {
        result.isNew = false;

        var results = m_Camera.getAllUnreadResults();
        for (var currentResult : results) {
            update(result, currentResult);
        }
    }

    @Override
    public void setReference(Pose2d pose) {
        m_Estimator.setReferencePose(pose);
    }

    @Override
    public String getName() {
        return m_Name;
    }

    @Override
    public Transform3d getOffset() {
        return m_Offset;
    }

    @Override
    public Simulator createSimulator(Specification specification) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSimulator'");
    }

}
