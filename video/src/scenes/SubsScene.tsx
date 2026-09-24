import React from "react";
import { AbsoluteFill, Easing, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { NavBar, ServiceIcon } from "../components/Phone";
import { PhoneStage } from "../components/PhoneStage";
import { C, CATEGORY, MONTH_TOTAL, SUBS, clamp } from "../theme";

export const SubsScene: React.FC = () => {
  const frame = useCurrentFrame();
  const total = interpolate(frame, [20, 70], [0, MONTH_TOTAL], {
    ...clamp,
    easing: Easing.bezier(0.16, 1, 0.3, 1),
  });
  return (
    <AbsoluteFill name="Subscriptions">
      <Backdrop />
      <Headline title="Every sub, one list" subtitle="Add, tag and sort in seconds" />
      <PhoneStage>
        <div style={{ padding: "8px 16px" }}>
          <div style={{ fontSize: 24, fontWeight: 700, margin: "8px 0 14px" }}>Subscriptions</div>
          <div
            style={{
              borderRadius: 20,
              padding: 20,
              background: `linear-gradient(135deg, ${C.brandDark}, ${C.brand}, ${C.brandDark}d9)`,
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14, fontWeight: 500, opacity: 0.85 }}>This month</span>
              <span
                style={{
                  fontSize: 12,
                  fontWeight: 600,
                  background: "rgba(255,255,255,0.2)",
                  borderRadius: 12,
                  padding: "4px 10px",
                }}
              >
                {SUBS.length} active
              </span>
            </div>
            <div style={{ marginTop: 10, display: "flex", alignItems: "baseline", gap: 8 }}>
              <span style={{ fontSize: 34, fontWeight: 700 }}>{total.toFixed(2)}</span>
              <span style={{ fontSize: 18, fontWeight: 600, opacity: 0.9 }}>USD</span>
            </div>
            <div style={{ fontSize: 12, opacity: 0.75, marginTop: 4 }}>Total scheduled this month</div>
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 10, marginTop: 14 }}>
            {SUBS.slice(0, 6).map((s, i) => {
              const start = 22 + i * 7;
              return (
                <div
                  key={s.name}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 14,
                    background: C.cardBg,
                    borderRadius: 16,
                    padding: 14,
                    opacity: interpolate(frame, [start, start + 10], [0, 1], clamp),
                    translate: interpolate(frame, [start, start + 18], ["120px 0px", "0px 0px"], {
                      ...clamp,
                      easing: Easing.bezier(0.16, 1, 0.3, 1),
                    }),
                  }}
                >
                  <ServiceIcon name={s.name} color={s.logo} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 16, fontWeight: 600 }}>{s.name}</div>
                    <div style={{ display: "flex", gap: 6, marginTop: 3, alignItems: "center" }}>
                      <span
                        style={{
                          fontSize: 11,
                          fontWeight: 500,
                          color: CATEGORY[s.category],
                          background: `${CATEGORY[s.category]}33`,
                          borderRadius: 6,
                          padding: "2px 6px",
                        }}
                      >
                        {s.category}
                      </span>
                      <span style={{ fontSize: 12, color: C.muted }}>{s.freq}</span>
                    </div>
                  </div>
                  <div style={{ fontSize: 15, fontWeight: 700 }}>{s.price.toFixed(2)} USD</div>
                </div>
              );
            })}
          </div>
        </div>
        <NavBar active={0} />
      </PhoneStage>
    </AbsoluteFill>
  );
};
