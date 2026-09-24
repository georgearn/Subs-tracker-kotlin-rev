import React from "react";
import { AbsoluteFill, Easing, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { NavBar } from "../components/Phone";
import { PhoneStage } from "../components/PhoneStage";
import { C, CATEGORY, SUBS, clamp } from "../theme";

const annual = (p: number, f: string) => (f === "Yearly" ? p : p * 12);
const byCat = Object.entries(
  SUBS.reduce<Record<string, number>>((acc, s) => {
    acc[s.category] = (acc[s.category] ?? 0) + annual(s.price, s.freq);
    return acc;
  }, {}),
).sort((a, b) => b[1] - a[1]);
const TOTAL = byCat.reduce((a, [, v]) => a + v, 0);

// Semicircle arc, matches ArcGauge.kt (180deg sweep, 26dp stroke)
const R = 107;
const CX = 120;
const CY = 135;
const LEN = Math.PI * R;

export const AnalyticsScene: React.FC = () => {
  const frame = useCurrentFrame();
  const sweep = interpolate(frame, [30, 80], [0, 1], {
    ...clamp,
    easing: Easing.bezier(0.33, 1, 0.68, 1),
  });
  let offset = 0;
  return (
    <AbsoluteFill name="Analytics">
      <Backdrop hue="#4DA673" />
      <Headline title="See where it goes" subtitle="Yearly spend by category & card" />
      <PhoneStage>
        <div style={{ padding: "8px 16px" }}>
          <div style={{ fontSize: 24, fontWeight: 700, margin: "8px 0 14px" }}>Analytics</div>
          <div style={{ display: "flex", background: C.cardBgElevated, borderRadius: 12, padding: 3, marginBottom: 10 }}>
            <span style={{ flex: 1, textAlign: "center", fontSize: 12, fontWeight: 700, color: C.muted, padding: 8 }}>
              By card
            </span>
            <span style={{ flex: 1, textAlign: "center", fontSize: 12, fontWeight: 700, background: C.brand, borderRadius: 10, padding: 8 }}>
              By category
            </span>
          </div>
          <div style={{ position: "relative", height: 180, display: "flex", justifyContent: "center" }}>
            <svg width={240} height={160} style={{ marginTop: 10 }}>
              <path
                d={`M ${CX - R} ${CY} A ${R} ${R} 0 0 1 ${CX + R} ${CY}`}
                stroke={C.cardBgElevated}
                strokeWidth={26}
                fill="none"
              />
              {byCat.map(([cat, v]) => {
                const seg = (v / TOTAL) * LEN;
                const start = offset;
                offset += seg;
                const visible = Math.max(0, Math.min(seg, sweep * LEN - start));
                return (
                  <path
                    key={cat}
                    d={`M ${CX - R} ${CY} A ${R} ${R} 0 0 1 ${CX + R} ${CY}`}
                    stroke={CATEGORY[cat]}
                    strokeWidth={26}
                    fill="none"
                    strokeDasharray={`0 ${start} ${visible} ${LEN}`}
                  />
                );
              })}
            </svg>
            <div style={{ position: "absolute", top: 90, textAlign: "center" }}>
              <div style={{ fontSize: 22, fontWeight: 700 }}>{(TOTAL * sweep).toFixed(2)} USD</div>
              <div style={{ fontSize: 12, color: C.muted, fontWeight: 500 }}>yearly spend</div>
            </div>
          </div>
          <div style={{ fontSize: 13, color: C.muted, margin: "6px 0 10px" }}>Total annual spend</div>
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {byCat.map(([cat, v], i) => {
              const s = 50 + i * 8;
              const w = interpolate(frame, [s, s + 25], [0, (v / byCat[0][1]) * 100], {
                ...clamp,
                easing: Easing.bezier(0.16, 1, 0.3, 1),
              });
              return (
                <div
                  key={cat}
                  style={{
                    background: C.cardBg,
                    borderRadius: 14,
                    padding: "12px 14px",
                    opacity: interpolate(frame, [s, s + 8], [0, 1], clamp),
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: 14, fontWeight: 600 }}>
                    <span style={{ display: "flex", alignItems: "center", gap: 8 }}>
                      <span style={{ width: 10, height: 10, borderRadius: 5, background: CATEGORY[cat] }} />
                      {cat}
                    </span>
                    <span>{v.toFixed(2)} USD</span>
                  </div>
                  <div style={{ marginTop: 8, height: 6, borderRadius: 3, background: C.cardBgElevated }}>
                    <div style={{ width: `${w}%`, height: 6, borderRadius: 3, background: CATEGORY[cat] }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
        <NavBar active={2} />
      </PhoneStage>
    </AbsoluteFill>
  );
};
