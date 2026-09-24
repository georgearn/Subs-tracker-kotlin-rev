import React from "react";
import { AbsoluteFill, Easing, Interactive, interpolate, useCurrentFrame } from "remotion";
import { Backdrop } from "../components/Headline";
import { C, fontFamily } from "../theme";

export const IntroScene: React.FC = () => {
  const frame = useCurrentFrame();
  return (
    <AbsoluteFill name="Intro" style={{ fontFamily }}>
      <Backdrop />
      <AbsoluteFill style={{ justifyContent: "center", alignItems: "center", gap: 56 }}>
        <Interactive.Div
          name="Logo"
          style={{
            width: 260,
            height: 260,
            borderRadius: 72,
            background: `linear-gradient(135deg, ${C.brandLight}, ${C.brand} 50%, ${C.brandDark})`,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 150,
            fontWeight: 800,
            color: "#fff",
            boxShadow: `0 40px 120px ${C.brand}88`,
            scale: interpolate(frame, [0, 24], [0.4, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
              easing: Easing.spring({ damping: 12 }),
              output: "perceptual-scale",
            }),
            rotate: interpolate(frame, [0, 24], ["-20deg", "0deg"], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
              easing: Easing.bezier(0.16, 1, 0.3, 1),
            }),
          }}
        >
          $
        </Interactive.Div>
        <Interactive.Div
          name="Title"
          style={{
            fontSize: 110,
            fontWeight: 800,
            letterSpacing: -3,
            lineHeight: 1,
            textAlign: "center",
            color: C.text,
            opacity: interpolate(frame, [14, 30], [0, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
            }),
            translate: interpolate(frame, [14, 36], ["0px 40px", "0px 0px"], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
              easing: Easing.bezier(0.16, 1, 0.3, 1),
            }),
          }}
        >
          Subscription
          <br />
          Tracker
        </Interactive.Div>
        <Interactive.Div
          name="Tagline"
          style={{
            fontSize: 48,
            fontWeight: 500,
            color: C.muted,
            opacity: interpolate(frame, [30, 46], [0, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
            }),
          }}
        >
          Every renewal. One place.
        </Interactive.Div>
      </AbsoluteFill>
    </AbsoluteFill>
  );
};
