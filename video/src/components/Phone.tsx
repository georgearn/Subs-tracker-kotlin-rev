import React from "react";
import { C, fontFamily } from "../theme";

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

const NAV = ["Subs", "Overview", "Analytics", "Settings"];

export const NavBar: React.FC<{ active: number }> = ({ active }) => (
  <div
    style={{
      position: "absolute",
      bottom: 0,
      left: 0,
      right: 0,
      height: 76,
      background: C.cardBg,
      display: "flex",
      justifyContent: "space-around",
      alignItems: "center",
      paddingBottom: 8,
    }}
  >
    {NAV.map((n, i) => (
      <div
        key={n}
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 4,
          color: i === active ? C.text : C.muted,
          fontSize: 11,
          fontWeight: 600,
        }}
      >
        <div
          style={{
            width: 56,
            height: 28,
            borderRadius: 14,
            background: i === active ? C.brand : "transparent",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <div
            style={{
              width: 16,
              height: 16,
              borderRadius: i === 2 ? 8 : 4,
              border: `2.5px solid ${i === active ? "#fff" : C.muted}`,
            }}
          />
        </div>
        {n}
      </div>
    ))}
  </div>
);

export const ServiceIcon: React.FC<{ name: string; color: string; size?: number }> = ({
  name,
  color,
  size = 46,
}) => (
  <div
    style={{
      width: size,
      height: size,
      borderRadius: size * 0.28,
      background: C.cardBgElevated,
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      color,
      fontWeight: 800,
      fontSize: size * 0.46,
      flexShrink: 0,
    }}
  >
    {name[0]}
  </div>
);
