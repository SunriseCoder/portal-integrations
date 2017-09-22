package app.integrations.audio;

public class ChannelOperation {
    private int inputChannel;
    private int outputChannel;
    private boolean adjust;

    public ChannelOperation(int inputChannel, int outputChannel, boolean adjust) {
        this.inputChannel = inputChannel;
        this.outputChannel = outputChannel;
        this.adjust = adjust;
    }

    public int getInputChannel() {
        return inputChannel;
    }

    public int getOutputChannel() {
        return outputChannel;
    }

    public boolean isAdjust() {
        return adjust;
    }
}
