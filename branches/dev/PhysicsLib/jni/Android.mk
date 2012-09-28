GDX_SRC_FILES += Box2D/Body.cpp \
Box2D/CircleShape.cpp \
Box2D/Contact.cpp \
Box2D/Fixture.cpp \
Box2D/Joint.cpp \
Box2D/PolygonShape.cpp \
Box2D/Shape.cpp \
Box2D/World.cpp \
Box2D/DistanceJoint.cpp \
Box2D/FrictionJoint.cpp \
Box2D/GearJoint.cpp \
Box2D/LineJoint.cpp \
Box2D/MouseJoint.cpp \
Box2D/PrismaticJoint.cpp \
Box2D/PulleyJoint.cpp \
Box2D/RevoluteJoint.cpp \
Box2D/Collision/b2BroadPhase.cpp \
Box2D/Collision/b2CollideCircle.cpp \
Box2D/Collision/b2CollidePolygon.cpp \
Box2D/Collision/b2Collision.cpp \
Box2D/Collision/b2Distance.cpp \
Box2D/Collision/b2DynamicTree.cpp \
Box2D/Collision/b2TimeOfImpact.cpp \
Box2D/Collision/Shapes/b2CircleShape.cpp \
Box2D/Collision/Shapes/b2PolygonShape.cpp \
Box2D/Common/b2BlockAllocator.cpp \
Box2D/Common/b2Math.cpp \
Box2D/Common/b2Settings.cpp \
Box2D/Common/b2StackAllocator.cpp \
Box2D/Dynamics/b2Body.cpp \
Box2D/Dynamics/b2ContactManager.cpp \
Box2D/Dynamics/b2Fixture.cpp \
Box2D/Dynamics/b2Island.cpp \
Box2D/Dynamics/b2World.cpp \
Box2D/Dynamics/b2WorldCallbacks.cpp \
Box2D/Dynamics/Contacts/b2CircleContact.cpp \
Box2D/Dynamics/Contacts/b2Contact.cpp \
Box2D/Dynamics/Contacts/b2ContactSolver.cpp \
Box2D/Dynamics/Contacts/b2PolygonAndCircleContact.cpp \
Box2D/Dynamics/Contacts/b2PolygonContact.cpp \
Box2D/Dynamics/Contacts/b2TOISolver.cpp \
Box2D/Dynamics/Joints/b2DistanceJoint.cpp \
Box2D/Dynamics/Joints/b2FrictionJoint.cpp \
Box2D/Dynamics/Joints/b2GearJoint.cpp \
Box2D/Dynamics/Joints/b2Joint.cpp \
Box2D/Dynamics/Joints/b2LineJoint.cpp \
Box2D/Dynamics/Joints/b2MouseJoint.cpp \
Box2D/Dynamics/Joints/b2PrismaticJoint.cpp \
Box2D/Dynamics/Joints/b2PulleyJoint.cpp \
Box2D/Dynamics/Joints/b2RevoluteJoint.cpp \
Box2D/Dynamics/Joints/b2WeldJoint.cpp

LOCAL_C_INCLUDES += $(LOCAL_PATH) $(LOCAL_PATH)/.. 

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE := box2d
LOCAL_SRC_FILES := $(GDX_SRC_FILES)
include $(BUILD_SHARED_LIBRARY)