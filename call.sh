#!/bin/bash

java -Xmx64m -Xms64m -cp bin:. update.Main

java -Xmx128m -Xms128m\
	-Dawt.useSystemAAFontSettings=on -Dswing.aatext=true \
	-Djavax.sound.sampled.Clip=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.Port=com.sun.media.sound.PortMixerProvider \
	-Djavax.sound.sampled.SourceDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.TargetDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-cp bin:. call.TestGui "$@"
