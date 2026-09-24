import React from "react";
import { linearTiming, springTiming, TransitionSeries } from "@remotion/transitions";
import { fade } from "@remotion/transitions/fade";
import { slide } from "@remotion/transitions/slide";
import { IntroScene } from "./scenes/IntroScene";
import { SubsScene } from "./scenes/SubsScene";
import { CalendarScene } from "./scenes/CalendarScene";
import { AnalyticsScene } from "./scenes/AnalyticsScene";
import { FeaturesScene } from "./scenes/FeaturesScene";
import { OutroScene } from "./scenes/OutroScene";

export const AppShowcase: React.FC = () => {
  return (
    <TransitionSeries>
      <TransitionSeries.Sequence durationInFrames={90} name="Intro">
        <IntroScene />
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition presentation={fade()} timing={linearTiming({ durationInFrames: 15 })} />
      <TransitionSeries.Sequence durationInFrames={150} name="Subscriptions">
        <SubsScene />
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition
        presentation={slide({ direction: "from-right" })}
        timing={springTiming({ config: { damping: 200 }, durationInFrames: 20 })}
      />
      <TransitionSeries.Sequence durationInFrames={150} name="Calendar">
        <CalendarScene />
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition
        presentation={slide({ direction: "from-right" })}
        timing={springTiming({ config: { damping: 200 }, durationInFrames: 20 })}
      />
      <TransitionSeries.Sequence durationInFrames={150} name="Analytics">
        <AnalyticsScene />
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition presentation={fade()} timing={linearTiming({ durationInFrames: 15 })} />
      <TransitionSeries.Sequence durationInFrames={140} name="Features">
        <FeaturesScene />
      </TransitionSeries.Sequence>
      <TransitionSeries.Transition presentation={fade()} timing={linearTiming({ durationInFrames: 15 })} />
      <TransitionSeries.Sequence durationInFrames={90} name="Outro">
        <OutroScene />
      </TransitionSeries.Sequence>
    </TransitionSeries>
  );
};
