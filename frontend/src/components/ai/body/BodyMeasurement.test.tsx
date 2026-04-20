import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BodyMeasurement } from "./BodyMeasurement";
import api from "@/lib/axios";

// Mocking axios
jest.mock("@/lib/axios");
const mockedApi = api as jest.Mocked<typeof api>;

// Mocking mediapipe
jest.mock("@mediapipe/pose", () => ({
  Pose: jest.fn().mockImplementation(() => ({
    setOptions: jest.fn(),
    onResults: jest.fn(),
    send: jest.fn(),
    close: jest.fn(),
  })),
}));

jest.mock("@mediapipe/camera_utils", () => ({
  Camera: jest.fn().mockImplementation(() => ({
    start: jest.fn(),
    stop: jest.fn(),
  })),
}));

describe("BodyMeasurement Component", () => {
  it("should send only JSON data to backend and NO image files", async () => {
    mockedApi.post.mockResolvedValue({ data: { success: true } });

    render(<BodyMeasurement />);

    // Start measurement
    const startButton = screen.getByText(/Start Measurement/i);
    fireEvent.click(startButton);

    // Capture (triggers handleCapture)
    const captureButton = await screen.findByText(/Capture & Calculate/i);
    fireEvent.click(captureButton);

    await waitFor(() => {
      expect(mockedApi.post).toHaveBeenCalledWith(
        "/users/profile/body",
        expect.objectContaining({
          chest: expect.any(Number),
          waist: expect.any(Number),
          hips: expect.any(Number),
        })
      );
    });

    // Verification: ensure no multipart/form-data or large image strings are in the call
    const callArguments = mockedApi.post.mock.calls[0];
    const payload = callArguments[1];
    
    // Check payload doesn't contain common image markers
    const payloadString = JSON.stringify(payload);
    expect(payloadString).not.toMatch(/data:image\/.*;base64/);
    expect(payloadString).not.toContain("blob:");
  });
});
