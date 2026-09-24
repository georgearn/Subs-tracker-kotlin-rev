import React from "react";
import { AbsoluteFill, Easing, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { NavBar, ServiceIcon } from "../components/Phone";
import { PhoneStage } from "../components/PhoneStage";
import { C, CATEGORY, MONTH_TOTAL, SUBS, clamp } from "../theme";

const FIRST_WEEKDAY = 1; // Sep 1 2026 is a Tuesday (Mon = 0)
const DAYS = 30;
const SELECTED = 24;

export const CalendarScene: React.FC = () => {
  const frame = useCurrentFrame();
  const selectAt = 70;
  const selected = frame >= selectAt;
  const shown = selected ? SUBS.filter((s) => s.day === SELECTED) : SUBS.slice(0, 4);
  return (
    <AbsoluteFill name="Calendar">
      <Backdrop hue="#338CBF" />
      <Headline title="Never miss a renewal" subtitle="Every payment on your calendar" />
      <PhoneStage>
        <div style={{ padding: "8px 16px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "8px 0 12px" }}>
            <span style={{ fontSize: 24, fontWeight: 700 }}>Overview</span>
            <div style={{ display: "flex", background: C.cardBgElevated, borderRadius: 12, padding: 3 }}>
              <span style={{ fontSize: 12, fontWeight: 700, background: C.brand, borderRadius: 10, padding: "6px 12px" }}>
                Monthly
              </span>
              <span style={{ fontSize: 12, fontWeight: 700, color: C.muted, padding: "6px 12px" }}>Yearly</span>
            </div>
          </div>
          <div style={{ background: C.cardBg, borderRadius: 18, padding: 14 }}>
            <div style={{ fontSize: 15, fontWeight: 700, marginBottom: 10 }}>September 2026</div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 4 }}>
              {["M", "T", "W", "T", "F", "S", "S"].map((d, i) => (
                <div key={i} style={{ textAlign: "center", fontSize: 11, color: C.muted, paddingBottom: 4 }}>
                  {d}
                </div>
              ))}
              {Array.from({ length: FIRST_WEEKDAY }).map((_, i) => (
                <div key={`e${i}`} />
              ))}
              {Array.from({ length: DAYS }).map((_, i) => {
                const day = i + 1;
                const subs = SUBS.filter((s) => s.day === day);
                const pop = 18 + i * 1.2;
                const isSel = selected && day === SELECTED;
                return (
                  <div
                    key={day}
                    style={{
                      height: 40,
                      borderRadius: 10,
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: 3,
                      fontSize: 13,
                      fontWeight: subs.length ? 700 : 500,
                      background: isSel ? C.brand : subs.length ? C.cardBgElevated : "transparent",
                      opacity: interpolate(frame, [pop, pop + 8], [0, 1], clamp),
                      scale: isSel
                        ? interpolate(frame, [selectAt, selectAt + 12], [0.8, 1], {
                            ...clamp,
                            easing: Easing.spring({ damping: 10 }),
                          })
                        : "1",
                    }}
                  >
                    {day}
                    <div style={{ display: "flex", gap: 2, height: 5 }}>
                      {subs.map((s) => (
                        <div
                          key={s.name}
                          style={{
                            width: 5,
                            height: 5,
                            borderRadius: 3,
                            background: isSel ? "#fff" : CATEGORY[s.category],
                          }}
                        />
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <div style={{ fontSize: 13, color: C.muted, margin: "14px 0 8px" }}>
            {selected ? `Payments on Sep ${SELECTED}` : "Payments this month"}
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
            {shown.map((s, i) => (
              <div
                key={s.name + selected}
                style={{
                  background: C.cardBg,
                  borderRadius: 14,
                  padding: 12,
                  display: "flex",
                  flexDirection: "column",
                  gap: 8,
                  opacity: interpolate(frame, [(selected ? selectAt : 50) + i * 4, (selected ? selectAt : 50) + i * 4 + 10], [0, 1], clamp),
                }}
              >
                <ServiceIcon name={s.name} color={s.logo} size={34} />
                <div style={{ fontSize: 14, fontWeight: 600 }}>{s.name}</div>
                <div style={{ fontSize: 13, fontWeight: 700, color: C.brandLight }}>{s.price.toFixed(2)} USD</div>
              </div>
            ))}
          </div>
          <div
            style={{
              marginTop: 12,
              background: C.cardBg,
              borderRadius: 14,
              padding: "12px 14px",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              opacity: interpolate(frame, [56, 66], [0, 1], clamp),
            }}
          >
            <span style={{ fontSize: 15, fontWeight: 700 }}>Total for September</span>
            <span style={{ fontSize: 18, fontWeight: 800, color: C.brandLight }}>{MONTH_TOTAL.toFixed(2)} USD</span>
          </div>
        </div>
        <NavBar active={1} />
      </PhoneStage>
    </AbsoluteFill>
  );
};
