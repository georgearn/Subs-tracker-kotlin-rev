import React from "react";
import { AbsoluteFill, Easing, Interactive, interpolate, useCurrentFrame } from "remotion";
import { Backdrop } from "../components/Headline";
import { C, fontFamily } from "../theme";

export const OutroScene: React.FC = () => {
  const frame = useCurrentFrame();
  return (
    <AbsoluteFill name="Outro" style={{ fontFamily }}>
      <Backdrop />
      <AbsoluteFill style={{ justifyContent: "center", alignItems: "center", gap: 48 }}>
        <Interactive.Div
          name="Outro logo"
          style={{
            width: 200,
            height: 200,
            borderRadius: 56,
            background: `linear-gradient(135deg, ${C.brandLight}, ${C.brand} 50%, ${C.brandDark})`,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 116,
            fontWeight: 800,
            color: "#fff",
            boxShadow: `0 40px 120px ${C.brand}88`,
            scale: interpolate(frame, [0, 20], [0.6, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
              easing: Easing.spring({ damping: 12 }),
              output: "perceptual-scale",
            }),
          }}
        >
          $
        </Interactive.Div>
        <Interactive.Div
          name="Outro title"
          style={{
            fontSize: 96,
            fontWeight: 800,
            letterSpacing: -2,
            textAlign: "center",
            lineHeight: 1.05,
            color: C.text,
            opacity: interpolate(frame, [8, 22], [0, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
            }),
          }}
        >
          Take control of
          <br />
          your subscriptions
        </Interactive.Div>
        <Interactive.Div
          name="Tech stack"
          style={{
            fontSize: 44,
            fontWeight: 500,
            color: C.muted,
            opacity: interpolate(frame, [22, 36], [0, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
            }),
          }}
        >
          Kotlin · Jetpack Compose · Material 3
        </Interactive.Div>
      </AbsoluteFill>
    </AbsoluteFill>
  );
};
