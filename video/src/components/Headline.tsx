import React from "react";
import { Easing, Interactive, interpolate, useCurrentFrame } from "remotion";
import { C, fontFamily } from "../theme";

export const Headline: React.FC<{ title: string; subtitle: string }> = ({
  title,
  subtitle,
}) => {
  const frame = useCurrentFrame();
  return (
    <div
      style={{
        position: "absolute",
        top: 130,
        left: 90,
        right: 90,
        fontFamily,
        textAlign: "center",
      }}
    >
      <Interactive.Div
        name="Headline"
        style={{
          fontSize: 92,
          fontWeight: 800,
          lineHeight: 1.05,
          letterSpacing: -2,
          color: C.text,
          opacity: interpolate(frame, [0, 18], [0, 1], {
            extrapolateLeft: "clamp",
            extrapolateRight: "clamp",
          }),
          translate: interpolate(frame, [0, 24], ["0px 40px", "0px 0px"], {
            extrapolateLeft: "clamp",
            extrapolateRight: "clamp",
            easing: Easing.bezier(0.16, 1, 0.3, 1),
          }),
        }}
      >
        {title}
      </Interactive.Div>
      <Interactive.Div
        name="Subheadline"
        style={{
          marginTop: 24,
          fontSize: 44,
          fontWeight: 500,
          color: C.muted,
          opacity: interpolate(frame, [8, 26], [0, 1], {
            extrapolateLeft: "clamp",
            extrapolateRight: "clamp",
          }),
        }}
      >
        {subtitle}
      </Interactive.Div>
    </div>
  );
};

export const Backdrop: React.FC<{ hue?: string }> = ({ hue = C.brand }) => (
  <div
    style={{
      position: "absolute",
      inset: 0,
      background: `radial-gradient(1200px 900px at 50% 110%, ${hue}55, transparent 70%), radial-gradient(900px 700px at 0% 0%, ${C.brandDark}44, transparent 70%), ${C.windowBg}`,
    }}
  />
);
