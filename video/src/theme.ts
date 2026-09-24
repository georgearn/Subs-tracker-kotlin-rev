import { loadFont } from "@remotion/fonts";
import { staticFile } from "remotion";

// Inter (variable, latin subset) bundled locally so renders need no network
export const fontFamily = "Inter";
loadFont({ family: fontFamily, url: staticFile("fonts/Inter.woff2"), weight: "100 900" });

// Dark palette from theme/Color.kt; accent + hero gradient sampled from real app screenshots
export const C = {
  accent: "#ADC9EA",
  // Marketing chrome (logo, backdrops) derived from the accent
  brand: "#5F7FA3",
  brandDark: "#3C546D",
  brandLight: "#ADC9EA",
  accentOn: "#EEF4FB",
  heroGradient: "linear-gradient(115deg, #405D79 0%, #7F9BBB 35%, #A3BFDF 50%, #7F9BBB 65%, #3C546D 100%)",
  windowBg: "#0C0C0F",
  cardBg: "#262630",
  cardBgElevated: "#32323E",
  text: "#F2F2F2",
  muted: "#9E9EAA",
};

export const CATEGORY: Record<string, string> = {
  Entertainment: "#D97333",
  Essentials: "#338CBF",
  Productivity: "#4DA673",
  "Quality of Life": "#9973CC",
  Streaming: "#CC4073",
  Random: "#BFB333",
  Other: "#8C8C94",
};

export type Sub = {
  name: string;
  category: keyof typeof CATEGORY;
  freq: string;
  price: number;
  card: string;
  day: number;
  icon: { bg: string; fg: string; glyph: string };
};

// Service names match the real screenshots; amounts are placeholders (blurred in the originals)
export const SUBS: Sub[] = [
  { name: "Adobe", category: "Productivity", freq: "Monthly", price: 22.99, card: "7764", day: 19, icon: { bg: "#FA0F00", fg: "#fff", glyph: "A" } },
  { name: "Amazon", category: "Quality of Life", freq: "Monthly", price: 8.99, card: "5515", day: 30, icon: { bg: "#232F3E", fg: "#FF9900", glyph: "a" } },
  { name: "Claude", category: "Productivity", freq: "Monthly", price: 20.0, card: "7764", day: 4, icon: { bg: "#D97706", fg: "#fff", glyph: "✳" } },
  { name: "ESET", category: "Essentials", freq: "Yearly", price: 39.99, card: "6838", day: 25, icon: { bg: "#3A8FC8", fg: "#fff", glyph: "E" } },
  { name: "Google", category: "Productivity", freq: "Monthly", price: 2.99, card: "3441", day: 10, icon: { bg: "#FFFFFF", fg: "#4285F4", glyph: "G" } },
  { name: "Netflix", category: "Streaming", freq: "Monthly", price: 15.49, card: "5515", day: 13, icon: { bg: "#141414", fg: "#E50914", glyph: "N" } },
  { name: "Spotify", category: "Streaming", freq: "Monthly", price: 10.99, card: "3441", day: 18, icon: { bg: "#1ED760", fg: "#0C0C0F", glyph: "≋" } },
  { name: "Youtube", category: "Streaming", freq: "Monthly", price: 13.99, card: "7764", day: 4, icon: { bg: "#FF0000", fg: "#fff", glyph: "▶" } },
];

export const BY_DAY = [...SUBS].sort((a, b) => a.day - b.day);

export const MONTH_TOTAL = SUBS.reduce((a, s) => a + s.price, 0);

export const clamp = {
  extrapolateLeft: "clamp",
  extrapolateRight: "clamp",
} as const;
