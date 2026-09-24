import React from "react";
import { C, Sub, fontFamily } from "../theme";
import { Icon } from "./Icons";

// Simple Android phone frame. Inner screen is 390x844 logical px, scaled up.
export const Phone: React.FC<{
  children: React.ReactNode;
  style?: React.CSSProperties;
}> = ({ children, style }) => {
  return (
    <div
      style={{
        width: 420,
        height: 874,
        borderRadius: 56,
        padding: 15,
        background: "linear-gradient(145deg, #3a3a46, #15151b)",
        boxShadow: "0 60px 120px rgba(0,0,0,0.6), 0 0 0 2px #4a4a58 inset",
        scale: "1.5",
        ...style,
      }}
    >
      <div
        style={{
          position: "relative",
          width: 390,
          height: 844,
          borderRadius: 42,
          overflow: "hidden",
          background: C.windowBg,
          fontFamily,
          color: C.text,
        }}
      >
        <div
          style={{
            height: 36,
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            padding: "0 26px",
            fontSize: 13,
            fontWeight: 600,
          }}
        >
          <span>9:41</span>
          <div
            style={{
              width: 12,
              height: 12,
              borderRadius: 6,
              background: "#000",
              boxShadow: "0 0 0 2px #222",
            }}
          />
          <span>100%</span>
        </div>
        {children}
      </div>
    </div>
  );
};

const NAV = [
  { label: "Subs", icon: "list" },
  { label: "Overview", icon: "calendarMonth" },
  { label: "Analytics", icon: "pie" },
  { label: "Settings", icon: "settings" },
] as const;

// Material 3 NavigationBar as in MainActivity.kt: pill indicator in accent, accent label
export const NavBar: React.FC<{ active: number }> = ({ active }) => (
  <div style={{ position: "absolute", bottom: 0, left: 0, right: 0 }}>
    <div
      style={{
        height: 76,
        background: C.cardBg,
        display: "flex",
        justifyContent: "space-around",
        alignItems: "center",
      }}
    >
      {NAV.map((n, i) => (
        <div
          key={n.label}
          style={{
            width: 80,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 4,
            color: i === active ? C.accent : C.muted,
            fontSize: 12,
            fontWeight: i === active ? 700 : 500,
          }}
        >
          <div
            style={{
              width: 60,
              height: 30,
              borderRadius: 15,
              background: i === active ? C.accent : "transparent",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Icon name={n.icon} size={22} color={i === active ? C.accentOn : C.muted} />
          </div>
          {n.label}
        </div>
      ))}
    </div>
    <div style={{ height: 22, background: C.windowBg, display: "flex", justifyContent: "center", alignItems: "center" }}>
      <div style={{ width: 108, height: 4, borderRadius: 2, background: "#fff" }} />
    </div>
  </div>
);

export const ServiceIcon: React.FC<{ sub: Sub; size?: number }> = ({ sub, size = 46 }) => (
  <div
    style={{
      width: size,
      height: size,
      borderRadius: size * 0.24,
      background: sub.icon.bg,
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      color: sub.icon.fg,
      fontWeight: 800,
      fontSize: size * 0.52,
      lineHeight: 1,
      flexShrink: 0,
    }}
  >
    {sub.icon.glyph}
  </div>
);
