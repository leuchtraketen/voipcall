java \
	-Djavax.sound.sampled.Clip=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.Port=com.sun.media.sound.PortMixerProvider \
	-Djavax.sound.sampled.SourceDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.TargetDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-cp bin:. call.TestClient "$@"

