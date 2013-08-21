#!/bin/bash

java -Xmx64m -Xms64m -cp bin:. update.Main

java -Xss256k -Xmx128m -Xms128m -Xincgc \
	-Dawt.useSystemAAFontSettings=on -Dswing.aatext=true \
	-Djavax.sound.sampled.Clip=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.Port=com.sun.media.sound.PortMixerProvider \
	-Djavax.sound.sampled.SourceDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-Djavax.sound.sampled.TargetDataLine=com.sun.media.sound.DirectAudioDeviceProvider \
	-cp bin:lib/commons-lang3-3.1.jar call.gui.Main "$@"
