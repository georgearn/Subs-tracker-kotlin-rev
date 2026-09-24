import React from "react";
import { AbsoluteFill, Easing, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { NavBar, ServiceIcon } from "../components/Phone";
import { PhoneStage } from "../components/PhoneStage";
import { Icon } from "../components/Icons";
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
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "8px 0 14px" }}>
            <span style={{ fontSize: 26, fontWeight: 700 }}>Subscriptions</span>
            <div style={{ width: 34, height: 34, borderRadius: 10, background: C.cardBgElevated, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="swapVert" size={18} color={C.text} />
            </div>
          </div>
          <div
            style={{
              borderRadius: 20,
              padding: 20,
              background: C.heroGradient,
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <span style={{ fontSize: 14, fontWeight: 500, opacity: 0.9 }}>This Month</span>
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
            <div style={{ fontSize: 12, opacity: 0.75, marginTop: 4 }}>Total scheduled payments for the current month</div>
          </div>
          <div style={{ fontSize: 13, fontWeight: 500, color: C.muted, margin: "16px 0 10px 4px" }}>
            Active Subscriptions ({SUBS.length})
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {SUBS.slice(0, 5).map((s, i) => {
              const start = 22 + i * 7;
              return (
                <div
                  key={s.name}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 11,
                    background: C.cardBg,
                    borderRadius: 16,
                    padding: 12,
                    opacity: interpolate(frame, [start, start + 10], [0, 1], clamp),
                    translate: interpolate(frame, [start, start + 18], ["120px 0px", "0px 0px"], {
                      ...clamp,
                      easing: Easing.bezier(0.16, 1, 0.3, 1),
                    }),
                  }}
                >
                  <ServiceIcon sub={s} size={42} />
                  <div style={{ flex: 1, minWidth: 0, whiteSpace: "nowrap", overflow: "hidden" }}>
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
                      <span style={{ fontSize: 11.5, color: C.muted, whiteSpace: "nowrap" }}>
                        {s.freq} • •••• {s.card}
                      </span>
                    </div>
                  </div>
                  <div style={{ fontSize: 14, fontWeight: 700, whiteSpace: "nowrap" }}>{s.price.toFixed(2)} USD</div>
                </div>
              );
            })}
          </div>
        </div>
        <div
          style={{
            position: "absolute",
            right: 16,
            bottom: 112,
            width: 56,
            height: 56,
            borderRadius: 28,
            background: C.accent,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            scale: interpolate(frame, [60, 72], [0, 1], { ...clamp, easing: Easing.spring({ damping: 12 }) }),
          }}
        >
          <Icon name="add" size={28} color="#fff" />
        </div>
        <NavBar active={0} />
      </PhoneStage>
    </AbsoluteFill>
  );
};
