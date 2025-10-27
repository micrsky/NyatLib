package icu.nyat.kusunoki.motd.shared;

import icu.nyat.kusunoki.NyatLib;
import icu.nyat.kusunoki.Utils.NyatLibLogger;

public interface StatusPingListener {

    default void handle(StatusPing ping) {
        try {
            ping.setVersionName("Nyatwork " + NyatLib.BrandVersion);
        } catch (UnsupportedOperationException ignored) {}
        try {
            ping.setVersionProtocol(NyatLib.BrandProtocolVersion);
        } catch (UnsupportedOperationException ignored) {}

        try {
            // Ensure current brand protocol is tracked; Set avoids duplicates
            NyatLib.ServerSupportedProtocolVersion.add(NyatLib.BrandProtocolVersion);
        } catch (Exception ex) {
            NyatLibLogger.logERROR(ex.toString());
        }

        if (NyatLib.ServerSupportedProtocolVersion != null && !NyatLib.ServerSupportedProtocolVersion.isEmpty()) {
            int clientProto;
            try {
                clientProto = ping.getClientProtocol();
            } catch (UnsupportedOperationException ex) {
                // Not supported on this platform
                return;
            }
            if (NyatLib.ServerSupportedProtocolVersion.contains(clientProto)) {
                try { ping.setVersionProtocol(clientProto); } catch (UnsupportedOperationException ignored) {}
            } else {
                try { ping.setVersionProtocol(NyatLib.BrandProtocolVersion); } catch (UnsupportedOperationException ignored) {}
            }
        }
    }
}
