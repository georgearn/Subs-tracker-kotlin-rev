import "./index.css";
import { Composition, Folder } from "remotion";
import { AppShowcase } from "./AppShowcase";
import { IntroScene } from "./scenes/IntroScene";
import { SubsScene } from "./scenes/SubsScene";
import { CalendarScene } from "./scenes/CalendarScene";
import { AnalyticsScene } from "./scenes/AnalyticsScene";
import { FeaturesScene } from "./scenes/FeaturesScene";
import { OutroScene } from "./scenes/OutroScene";

// 90 + 150*3 + 140 + 90 - (15 + 20 + 20 + 15 + 15) = 685
export const RemotionRoot: React.FC = () => {
  return (
    <>
      <Composition id="AppShowcase" component={AppShowcase} durationInFrames={685} fps={30} width={1080} height={1920} />
      <Folder name="AppShowcase-Scenes">
        <Composition id="Intro" component={IntroScene} durationInFrames={90} fps={30} width={1080} height={1920} />
        <Composition id="Subscriptions" component={SubsScene} durationInFrames={150} fps={30} width={1080} height={1920} />
        <Composition id="Calendar" component={CalendarScene} durationInFrames={150} fps={30} width={1080} height={1920} />
        <Composition id="Analytics" component={AnalyticsScene} durationInFrames={150} fps={30} width={1080} height={1920} />
        <Composition id="Features" component={FeaturesScene} durationInFrames={140} fps={30} width={1080} height={1920} />
        <Composition id="Outro" component={OutroScene} durationInFrames={90} fps={30} width={1080} height={1920} />
      </Folder>
    </>
  );
};
