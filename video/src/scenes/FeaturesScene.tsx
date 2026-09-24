import React from "react";
import { AbsoluteFill, Easing, Interactive, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { ServiceIcon } from "../components/Phone";
import { C, SUBS, fontFamily, clamp } from "../theme";

const FEATURES = [
  { title: "100% offline", body: "Stored locally with Room. No account, no tracking.", color: "#4DA673" },
  { title: "Multi-currency", body: "Enter any currency, see totals in yours.", color: "#338CBF" },
  { title: "Your language", body: "Instant in-app switching, English & Russian.", color: "#D97333" },
];

export const FeaturesScene: React.FC = () => {
  const frame = useCurrentFrame();
  return (
    <AbsoluteFill name="Features" style={{ fontFamily, color: C.text }}>
      <Backdrop hue="#CC4073" />
      <Headline title="Reminders before you pay" subtitle="Cancel before the charge, not after" />
      <Interactive.Div
        name="Notification"
        style={{
          position: "absolute",
          top: 560,
          left: 90,
          right: 90,
          background: "rgba(50,50,62,0.92)",
          borderRadius: 40,
          padding: 36,
          display: "flex",
          gap: 30,
          alignItems: "center",
          boxShadow: "0 30px 80px rgba(0,0,0,0.5)",
          translate: interpolate(frame, [10, 34], ["0px -260px", "0px 0px"], {
            extrapolateLeft: "clamp",
            extrapolateRight: "clamp",
            easing: Easing.spring({ damping: 14 }),
          }),
          opacity: interpolate(frame, [10, 20], [0, 1], {
            extrapolateLeft: "clamp",
            extrapolateRight: "clamp",
          }),
        }}
      >
        <ServiceIcon sub={SUBS.find((s) => s.name === "Netflix")!} size={110} />
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: 30, color: C.muted, fontWeight: 500 }}>Subscription Tracker · now</div>
          <div style={{ fontSize: 44, fontWeight: 700, marginTop: 6 }}>Netflix renews tomorrow</div>
          <div style={{ fontSize: 36, color: C.muted, marginTop: 4 }}>15.49 USD · card •••• 5515</div>
        </div>
      </Interactive.Div>
      <div
        style={{
          position: "absolute",
          top: 900,
          left: 90,
          right: 90,
          display: "flex",
          flexDirection: "column",
          gap: 36,
        }}
      >
        {FEATURES.map((f, i) => {
          const s = 40 + i * 12;
          return (
            <div
              key={f.title}
              style={{
                background: C.cardBg,
                borderRadius: 36,
                padding: "40px 44px",
                borderLeft: `12px solid ${f.color}`,
                opacity: interpolate(frame, [s, s + 12], [0, 1], clamp),
                translate: interpolate(frame, [s, s + 22], ["-120px 0px", "0px 0px"], {
                  ...clamp,
                  easing: Easing.bezier(0.16, 1, 0.3, 1),
                }),
              }}
            >
              <div style={{ fontSize: 56, fontWeight: 800 }}>{f.title}</div>
              <div style={{ fontSize: 40, color: C.muted, marginTop: 8 }}>{f.body}</div>
            </div>
          );
        })}
      </div>
    </AbsoluteFill>
  );
};
