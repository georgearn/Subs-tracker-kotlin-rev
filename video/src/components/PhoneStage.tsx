import React from "react";
import { Easing, interpolate, useCurrentFrame } from "remotion";
import { Phone } from "./Phone";

// Positions the phone below the headline and slides it up on entry.
export const PhoneStage: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const frame = useCurrentFrame();
  return (
    <div
      style={{
        position: "absolute",
        left: 330,
        top: 710,
        translate: interpolate(frame, [4, 34], ["0px 500px", "0px 0px"], {
          extrapolateLeft: "clamp",
          extrapolateRight: "clamp",
          easing: Easing.bezier(0.16, 1, 0.3, 1),
        }),
        opacity: interpolate(frame, [4, 16], [0, 1], {
          extrapolateLeft: "clamp",
          extrapolateRight: "clamp",
        }),
      }}
    >
      <Phone>{children}</Phone>
    </div>
  );
};
