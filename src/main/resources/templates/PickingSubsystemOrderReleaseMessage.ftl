<PickingSubSystemOrderReleaseMessage xmlns="http://www.xmlns.walmartstores.com/SuppyChain/FulfillmentManagement/GlobalIntegeratedFulfillment/Picking/PickingSubSystemOrderReleaseMessage/1.0/" xmlns:hdr="http://www.xmlns.walmartstores.com/Header/datatypes/MessageHeader/1.4/">
  <hdr:MessageHeader>
    <hdr:SubId>SUB-EIC-UPDPICK-V1</hdr:SubId>
    <hdr:CnsmrId>CON-NODE-UPDPICK-V1</hdr:CnsmrId>
    <hdr:SrvcNm>UpdateFulfillmentPicking.subSystemOrderRelease</hdr:SrvcNm>
    <hdr:TranId>${order.fulfillmentOrder.orderNbr}</hdr:TranId>
    <hdr:Version>1.0</hdr:Version>
  </hdr:MessageHeader>
  <MessageBody>
    <RoutingInfo>
      <SourceNode>
        <location>
          <countryCode>${routingInfo.sourceNode.location.countryCode}</countryCode>
        </location>
        <nodeID>${routingInfo.sourceNode.nodeID}</nodeID>
      </SourceNode>
      <DestinationNode>
        <location>
          <countryCode>${routingInfo.destinationNode.location.countryCode}</countryCode>
        </location>
        <nodeID>${routingInfo.destinationNode.nodeID}</nodeID>
      </DestinationNode>
    </RoutingInfo>
    <Order>
      <node>
        <nodeId>${order.node.nodeId}</nodeId>
        <countryCode>${order.node.countryCode}</countryCode>
      </node>
      <fulfillmentOrder>
        <orderNbr>${order.fulfillmentOrder.orderNbr}</orderNbr>
        <orderPriority>
          <code>1</code>
          <description>Grocery Pickup Default</description>
        </orderPriority>
        <type code="6" name="${order.fulfillmentOrder.type.name}"/>
        <destinationBusinessUnit destBannerName="Walmart Grocery" destDivisonNumber="1"/>
        <pickDueTime>${order.fulfillmentOrder.pickDueTime}</pickDueTime>
        <expectedOrderPickupTime>${order.fulfillmentOrder.expectedOrderPickupTime}</expectedOrderPickupTime>
        <earliestPickTime>${order.fulfillmentOrder.earliestPickTime}</earliestPickTime>
        <orderSequenceNumber>${order.fulfillmentOrder.orderSequenceNumber}</orderSequenceNumber>
        <loadGroupNumber>${order.fulfillmentOrder.loadGroupNumber}</loadGroupNumber>
        <carrierBagAllowed>${order.fulfillmentOrder.carrierBagAllowed}</carrierBagAllowed>
        <recordCarrierBagCount>${order.fulfillmentOrder.recordCarrierBagCount}</recordCarrierBagCount>

        <lines>
                <line>
                    <lineNbr>${order.fulfillmentOrder.lines.line.lineNbr}</lineNbr>
                    <upc>${order.fulfillmentOrder.lines.line.upc}</upc>
                    <qty>${order.fulfillmentOrder.lines.line.qty}</qty>
                    <weight>${order.fulfillmentOrder.lines.line.weight}</weight>
                    <pickByType>${order.fulfillmentOrder.lines.line.pickByType}</pickByType>
                    <substitutionAllowed>${order.fulfillmentOrder.lines.line.substitutionAllowed}</substitutionAllowed>
                    <itemNbr>${order.fulfillmentOrder.lines.line.itemNbr}</itemNbr>
                    <uom>${order.fulfillmentOrder.lines.line.itemNbr}</uom>
                </line>
        </lines>
      </fulfillmentOrder>
    </Order>

    <MessageExtensions>
          <messageExtension>
              <name>${messageExtensions.messageExtension.name}</name>
              <value>${messageExtensions.messageExtension.value}</value>
          </messageExtension>
    </MessageExtensions>
  </MessageBody>
</PickingSubSystemOrderReleaseMessage>
