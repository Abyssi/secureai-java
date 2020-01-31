package com.secureai.rl.vi;

import com.secureai.rl.abs.DiscreteState;
import com.secureai.rl.abs.SMDP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.space.DiscreteSpace;

import java.util.HashMap;
import java.util.logging.Logger;

public class ValueIteration<O extends DiscreteState> {

    private final static Logger LOGGER = Logger.getLogger(ValueIteration.class.getName());

    private ValueIteration.VIConfiguration conf;
    private SMDP<O, Integer, DiscreteSpace> mdp;

    private HashMap<Integer, Double> V = new HashMap<>(); // State: Utility
    private HashMap<Integer, Integer> P = new HashMap<>(); // State: Action

    public ValueIteration(SMDP<O, Integer, DiscreteSpace> mdp, ValueIteration.VIConfiguration conf) {
        this.mdp = mdp;
        this.conf = conf;
    }

    public int chooseBest(O state) {
        int bestAction = -1;
        double bestQ = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < this.mdp.getActionSpace().getSize(); a++) {
            this.mdp.setState(state);
            StepReply<O> step = this.mdp.step(a);
            double q = step.getReward() + this.conf.gamma * this.V.getOrDefault(state.toInt(), .1d);
            if (q > bestQ) {
                bestQ = q;
                bestAction = a;
            }
        }

        return bestAction;
    }

    public int choose(O state) {
        return this.P.getOrDefault(state.toInt(), this.mdp.getActionSpace().randomAction());
    }

    public void solve() {
        LOGGER.info("[Solve] Starting");
        for (int i = 0; i < this.conf.iterations; i++) {
            double vDelta = 0;
            for (int s = 0; s < Math.pow(this.mdp.getObservationSpace().getShape()[0], 2); s++) {
                this.mdp.getState().setFromInt(s);
                double previousV = V.getOrDefault(s, 0d);
                int bestAction = this.chooseBest(this.mdp.getState());
                StepReply<O> step = this.mdp.step(bestAction);
                this.V.put(s, step.getReward() + this.conf.gamma * this.V.getOrDefault(step.getObservation().toInt(), .1d));
                this.P.put(s, bestAction);
                vDelta = Math.max(vDelta, Math.abs(previousV - this.V.get(s)));
            }
            if (vDelta < this.conf.epsilon)
                break;
        }
    }

    public double play() {
        O state = this.mdp.reset();
        double rewards = 0;
        int i = 0;
        for (; !this.mdp.isDone(); i++) {
            StepReply<O> step = this.mdp.step(this.choose(state));
            state = step.getObservation();
            rewards += step.getReward();
        }
        LOGGER.info(String.format("[Play] Episode: %d; Reward: %f; Average: %f", i, rewards, rewards / i));
        return rewards;
    }

    public double evaluate(int episodes) {
        double rewards = 0;
        int i = 0;
        for (; i < episodes; i++) {
            double reward = this.play();
            rewards += reward;
        }

        LOGGER.info(String.format("[Evaluate] Average: %f", rewards / i));
        return rewards / i;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class VIConfiguration {
        int seed;
        int iterations;
        double gamma;
        double epsilon;
    }
}
