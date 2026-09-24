import { loadFont } from "@remotion/fonts";
import { staticFile } from "remotion";

// Inter (variable, latin subset) bundled locally so renders need no network
export const fontFamily = "Inter";
loadFont({ family: fontFamily, url: staticFile("fonts/Inter.woff2"), weight: "100 900" });

// Mirrors app/src/main/java/com/example/theme/Color.kt (dark palette)
export const C = {
  brand: "#7359E0",
  brandDark: "#4D389E",
  brandLight: "#907BF0",
  windowBg: "#0C0C0F",
  cardBg: "#262630",
  cardBgElevated: "#32323E",
  text: "#F2F2F2",
  muted: "#9E9EAA",
  green: "#2E9E57",
  red: "#BD4040",
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
  card?: string;
  day: number;
  logo: string;
};

export const SUBS: Sub[] = [
  { name: "Netflix", category: "Streaming", freq: "Monthly", price: 15.49, card: "4821", day: 3, logo: "#E50914" },
  { name: "Spotify", category: "Entertainment", freq: "Monthly", price: 10.99, card: "4821", day: 7, logo: "#1DB954" },
  { name: "iCloud+", category: "Essentials", freq: "Monthly", price: 2.99, card: "9034", day: 12, logo: "#3693F3" },
  { name: "Notion", category: "Productivity", freq: "Monthly", price: 10.0, card: "9034", day: 15, logo: "#FFFFFF" },
  { name: "Duolingo", category: "Quality of Life", freq: "Yearly", price: 7.0, day: 21, logo: "#58CC02" },
  { name: "ChatGPT", category: "Productivity", freq: "Monthly", price: 20.0, card: "4821", day: 24, logo: "#10A37F" },
  { name: "Disney+", category: "Streaming", freq: "Monthly", price: 13.99, card: "9034", day: 28, logo: "#113CCF" },
];

export const MONTH_TOTAL = SUBS.reduce((a, s) => a + s.price, 0);

export const clamp = {
  extrapolateLeft: "clamp",
  extrapolateRight: "clamp",
} as const;
