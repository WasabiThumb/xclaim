package io.github.wasabithumb.xclaim.claim;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * State attached to a Claim for exclusive usage by the ClaimManager
 */
@ApiStatus.Internal
class ClaimState {

    final Lock lock = new ReentrantLock();

    String attachedName = null;

    Set<Long> attachedRegions = Collections.emptySet();

}
