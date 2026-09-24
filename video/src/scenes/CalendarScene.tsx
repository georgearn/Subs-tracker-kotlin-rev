import React from "react";
import { AbsoluteFill, Easing, interpolate, useCurrentFrame } from "remotion";
import { Backdrop, Headline } from "../components/Headline";
import { Icon } from "../components/Icons";
import { NavBar, ServiceIcon } from "../components/Phone";
import { PhoneStage } from "../components/PhoneStage";
import { BY_DAY, C, CATEGORY, Sub, clamp } from "../theme";

// Sep 1 2026 is a Tuesday
const WEEKDAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
const CHIP_W = 52;
const CHIP_GAP = 8;
const SELECTED = 13;
const TAP_AT = 78;

const MonthSubCard: React.FC<{ sub: Sub; opacity: number }> = ({ sub, opacity }) => {
  const cat = CATEGORY[sub.category];
  return (
    <div style={{ background: C.cardBg, borderRadius: 16, padding: 12, height: 104, opacity }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
        <ServiceIcon sub={sub} size={36} />
        <span
          style={{
            fontSize: 11,
            fontWeight: 600,
            color: cat,
            background: `${cat}33`,
            borderRadius: 8,
            padding: "3px 7px",
          }}
        >
          Sep {String(sub.day).padStart(2, "0")}
        </span>
      </div>
      <div style={{ fontSize: 14, fontWeight: 600, marginTop: 10 }}>{sub.name}</div>
      <div style={{ fontSize: 13, color: "rgba(242,242,242,0.9)", marginTop: 4 }}>{sub.price.toFixed(2)} USD</div>
    </div>
  );
};

export const CalendarScene: React.FC = () => {
  const frame = useCurrentFrame();
  const selected = frame >= TAP_AT;
  // Day strip scrolls so day 13 comes into view, then gets tapped
  const scrollX = interpolate(frame, [36, 70], [0, -(SELECTED - 4) * (CHIP_W + CHIP_GAP)], {
    ...clamp,
    easing: Easing.bezier(0.65, 0, 0.35, 1),
  });
  const shown = selected ? BY_DAY.filter((s) => s.day === SELECTED) : BY_DAY;
  const listStart = selected ? TAP_AT : 20;
  return (
    <AbsoluteFill name="Calendar">
      <Backdrop hue="#338CBF" />
      <Headline title="Never miss a renewal" subtitle="Every payment, day by day" />
      <PhoneStage>
        <div style={{ padding: "8px 16px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "10px 0 14px" }}>
            <span style={{ fontSize: 24, fontWeight: 700 }}>Overview</span>
            <div style={{ display: "flex", background: C.cardBgElevated, borderRadius: 12, padding: 3 }}>
              <span style={{ fontSize: 12, fontWeight: 700, color: "#fff", background: C.accent, borderRadius: 10, padding: "6px 12px" }}>
                Monthly
              </span>
              <span style={{ fontSize: 12, fontWeight: 700, color: C.muted, padding: "6px 12px" }}>Yearly</span>
            </div>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: 8,
                background: C.cardBgElevated,
                borderRadius: 12,
                padding: "9px 12px",
              }}
            >
              <Icon name="calendarToday" size={16} color={C.accent} />
              <span style={{ fontSize: 15, fontWeight: 700 }}>September 2026</span>
            </div>
            <span style={{ fontSize: 13, color: C.muted }}>None due today</span>
          </div>
          <div style={{ overflow: "hidden", margin: "14px 0 16px" }}>
            <div style={{ display: "flex", gap: CHIP_GAP, translate: `${scrollX}px 0px` }}>
              {Array.from({ length: 30 }).map((_, i) => {
                const day = i + 1;
                const has = BY_DAY.some((s) => s.day === day);
                const isSel = selected && day === SELECTED;
                return (
                  <div
                    key={day}
                    style={{
                      width: CHIP_W,
                      height: 76,
                      flexShrink: 0,
                      borderRadius: 14,
                      background: isSel ? C.accent : C.cardBg,
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      justifyContent: "center",
                      scale: isSel
                        ? interpolate(frame, [TAP_AT, TAP_AT + 12], [0.85, 1], {
                            ...clamp,
                            easing: Easing.spring({ damping: 10 }),
                          })
                        : "1",
                    }}
                  >
                    <span style={{ fontSize: 11, fontWeight: 500, color: isSel ? "rgba(255,255,255,0.9)" : C.muted }}>
                      {WEEKDAYS[(day) % 7]}
                    </span>
                    <span style={{ fontSize: 16, fontWeight: 700, marginTop: 4, color: isSel ? "#fff" : C.text }}>{day}</span>
                    <div
                      style={{
                        marginTop: 6,
                        width: has ? 8 : 0,
                        height: 2.5,
                        borderRadius: 2,
                        background: isSel ? "#fff" : C.accent,
                      }}
                    />
                  </div>
                );
              })}
            </div>
          </div>
          <div style={{ fontSize: 13, fontWeight: 500, color: C.muted, margin: "0 0 12px 4px" }}>
            {selected ? `Due on Sep ${SELECTED} (${shown.length})` : `All in September (${BY_DAY.length})`}
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            {shown.map((s, i) => (
              <MonthSubCard
                key={s.name + selected}
                sub={s}
                opacity={interpolate(frame, [listStart + i * 4, listStart + i * 4 + 10], [0, 1], clamp)}
              />
            ))}
          </div>
        </div>
        <NavBar active={1} />
      </PhoneStage>
    </AbsoluteFill>
  );
};
