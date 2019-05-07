package app.integrations.utils;

import javax.sound.sampled.AudioFormat;

public class AudioFormatHelper {

    public static AudioFormat copyFormat(AudioFormat sourceFormat, int channelsCount) {
        float sampleRate = sourceFormat.getSampleRate();
        int sampleSizeInBits = sourceFormat.getSampleSizeInBits();
        boolean bigEndian = sourceFormat.isBigEndian();
        AudioFormat copyOfFormat = new AudioFormat(sampleRate, sampleSizeInBits , channelsCount, true , bigEndian);
        return copyOfFormat;
    }
}
